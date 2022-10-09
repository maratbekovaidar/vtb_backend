package kz.kalybayevv.VtbNews.services.impl;

import kz.kalybayevv.VtbNews.constants.Constants;
import kz.kalybayevv.VtbNews.constants.Errors;
import kz.kalybayevv.VtbNews.entity.Role;
import kz.kalybayevv.VtbNews.entity.User;
import kz.kalybayevv.VtbNews.exceptions.FLCException;
import kz.kalybayevv.VtbNews.repositories.RoleRepository;
import kz.kalybayevv.VtbNews.repositories.UserRepository;
import kz.kalybayevv.VtbNews.services.MailService;
import kz.kalybayevv.VtbNews.services.UserCommonService;
import kz.kalybayevv.VtbNews.utils.IdGenerator;
import kz.kalybayevv.VtbNews.utils.ObjectMapper;
import kz.kalybayevv.VtbNews.utils.SecurityUtils;
import kz.kalybayevv.VtbNews.utils.StringUtils;
import kz.kalybayevv.VtbNews.web.dto.ResponseDto;
import kz.kalybayevv.VtbNews.web.dto.RoleDto;
import kz.kalybayevv.VtbNews.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Service for saving, reading, creating and deleting user data.
 * <p>
 * We use the @SneakyThrows annotation to sneakily throw checked exceptions.
 */
@Service
@RequiredArgsConstructor
public class IUserCommonService implements UserCommonService {
    private final Logger log = LoggerFactory.getLogger(IUserCommonService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisTemplate<Object, Object> redisTemplate;
    private ValueOperations<Object, Object> cachedValues;

    /**
     * Install dependency of RedisTemplate
     */
    @PostConstruct
    public void setUp() {
        cachedValues = redisTemplate.opsForValue();
    }

    @Override
    @SneakyThrows
    public ResponseDto<?> register(UserDto dto) {
        if (StringUtils.isAnyEmpty(dto.getFirstname().trim(), dto.getLastname().trim(),
                dto.getEmail().trim(), dto.getPassword().trim())) {
            throw new FLCException(Errors.ERR_VALIDATION);
        }
        Optional<User> userFromDb = userRepository.findByEmail(dto.getEmail().trim());
        if (userFromDb.isPresent()) {
            return ResponseDto.builder()
                    .success(Boolean.FALSE)
                    .errMessage(Errors.USER_EXISTS)
                    .httpStatus(HttpStatus.BAD_REQUEST.value())
                    .build();
        }
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoles()
                .stream()
                .map(RoleDto::getId)
                .collect(Collectors.toSet())));
        if (roles.isEmpty()) {
            throw new FLCException(Errors.ERR_INTERNAL_SERVER_ERROR);
        }
        User user = User.builder()
                .firstname(dto.getFirstname().trim())
                .lastname(dto.getLastname().trim())
                .activated(Boolean.FALSE)
                .email(dto.getEmail().trim())
                .password(passwordEncoder.encode(dto.getPassword().trim()))
                .deleted(Boolean.FALSE)
                .roles(roles)
                .build();
        String base62code = IdGenerator.getBase62(Constants.CONSTANT_ACT_DIGIT);
        Future<Boolean> success = mailService.sendEmail(dto.getEmail().trim(),
                Constants.TEXT_ACT_SUBJECT, Constants.CONTENT_ACT + base62code);
        if (BooleanUtils.isTrue(success.get())) {
            cachedValues.set(dto.getEmail().trim(), base62code);
            user = userRepository.save(user);
            return ResponseDto.builder()
                    .success(Boolean.TRUE)
                    .data(ObjectMapper.convertToUserDto(user))
                    .httpStatus(HttpStatus.OK.value())
                    .build();
        }
        return ResponseDto.builder()
                .success(Boolean.FALSE)
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errMessage(Errors.ERR_INTERNAL_SERVER_ERROR)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserAuthorities(Long id) {
        if (Objects.nonNull(id)) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                return user.get().getRoles().stream().map(r -> Constants.ROLE_PREFIX + r.getName()).collect(Collectors.toSet());
            }
        }
        return new HashSet<>();
    }

    @Override
    @SneakyThrows
    @Transactional
    public ResponseDto<?> activate(String email, String code) {
        if (StringUtils.isEmpty(email.trim())) {
            throw new FLCException(Errors.ERR_VALIDATION);
        }
        Optional<User> userFromDb = userRepository.findByEmail(email.trim());
        if (userFromDb.isPresent()) {
            if (StringUtils.isNotEmpty(code)) {
                if (Objects.equals(cachedValues.get(email.trim()), code)) {
                    User user = userFromDb.get();
                    if (BooleanUtils.isTrue(user.getDeleted()) || BooleanUtils.isTrue(user.getActivated())) {
                        throw new FLCException(Errors.FORBIDDEN_ACTION);
                    }
                    cachedValues.getOperations().delete(email.trim());
                    user.setActivated(Boolean.TRUE);
                    user = userRepository.save(user);
                    return ResponseDto.builder()
                            .data(ObjectMapper.convertToUserDto(user))
                            .success(Boolean.TRUE)
                            .httpStatus(HttpStatus.OK.value())
                            .build();

                }
                return ResponseDto.builder()
                        .success(Boolean.FALSE)
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .errMessage(Errors.ERR_VALIDATION)
                        .build();
            } else {
                if (BooleanUtils.isFalse(userFromDb.get().getActivated())) {
                    String base62code = IdGenerator.getBase62(Constants.CONSTANT_ACT_DIGIT);
                    Future<Boolean> success = mailService.sendEmail(email.trim(),
                            Constants.TEXT_ACT_SUBJECT, Constants.CONTENT_ACT + base62code);
                    if (BooleanUtils.isTrue(success.get())) {
                        if (StringUtils.isNotEmpty((String) cachedValues.get(email.trim()))) {
                            cachedValues.getAndSet(email.trim(), base62code);
                        } else {
                            cachedValues.set(email.trim(), base62code);
                        }
                        return ResponseDto.builder()
                                .success(Boolean.TRUE)
                                .data(userFromDb.get().getId())
                                .httpStatus(HttpStatus.OK.value())
                                .build();
                    }
                    return ResponseDto.builder()
                            .success(Boolean.FALSE)
                            .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .errMessage(Errors.ERR_INTERNAL_SERVER_ERROR)
                            .build();
                }
                return ResponseDto.builder()
                        .httpStatus(HttpStatus.FORBIDDEN.value())
                        .errMessage(Errors.FORBIDDEN_ACTION)
                        .success(Boolean.FALSE)
                        .build();
            }
        }
        return ResponseDto.builder()
                .success(Boolean.FALSE)
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .errMessage(Errors.NO_USER)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<?> getUserInformation(Long id) {
        Optional<User> userFromDb;
        if (id != null) {
            userFromDb = userRepository.findById(id);
            if (userFromDb.isPresent()) {
                User user = userFromDb.get();
                if (BooleanUtils.isFalse(user.getDeleted())) {
                    return ResponseDto.builder()
                            .success(Boolean.TRUE)
                            .httpStatus(HttpStatus.OK.value())
                            .data(ObjectMapper.convertToUserDto(user))
                            .build();
                }
            }
            return ResponseDto.builder()
                    .success(Boolean.FALSE)
                    .httpStatus(HttpStatus.NO_CONTENT.value())
                    .errMessage(Errors.NO_USER)
                    .build();
        }
        userFromDb = userRepository.findByEmail(SecurityUtils.getCurrentUserLoginOrThrow());
        User user = userFromDb.get();
        return ResponseDto.builder()
                .success(Boolean.TRUE)
                .httpStatus(HttpStatus.OK.value())
                .data(ObjectMapper.convertToUserDto(user))
                .build();
    }

    @Override
    @Transactional
    public ResponseDto<?> updateUser(UserDto dto) {
        Optional<User> userFromDb;
        boolean success = Boolean.FALSE;
        if (dto.getId() != null) {
            userFromDb = userRepository.findById(dto.getId());
            if (userFromDb.isPresent()) {
                if (BooleanUtils.isFalse(userFromDb.get().getDeleted()) && BooleanUtils
                        .isTrue(userFromDb.get().getActivated()))
                    success = Boolean.TRUE;
            }
        } else {
            userFromDb = userRepository.findByEmail(SecurityUtils.getCurrentUserLoginOrThrow());
            if (userFromDb.isPresent()) {
                success = Boolean.TRUE;
            }
        }
        if (BooleanUtils.isTrue(success)) {
            User user = userFromDb.get();
            if (StringUtils.isNotEmpty(dto.getFirstname().trim())) {
                user.setFirstname(dto.getFirstname().trim());
            }
            if (StringUtils.isNotEmpty(dto.getLastname().trim())) {
                user.setLastname(dto.getLastname().trim());
            }
            user = userRepository.save(user);
            return ResponseDto.builder()
                    .success(Boolean.TRUE)
                    .data(ObjectMapper.convertToUserDto(user))
                    .httpStatus(HttpStatus.OK.value())
                    .build();
        }
        return ResponseDto.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .success(Boolean.FALSE)
                .errMessage(Errors.ERR_ACCESS_DENIED)
                .build();
    }

    @Override
    @Transactional
    public ResponseDto<?> resetPassword(String oldPassword, String newPassword, String email) {
        return null;
    }

    @Override
    @Transactional
    public ResponseDto<?> deleteUser(Long id) {
        Optional<User> userFromDb;
        if (id != null) {
            userFromDb = userRepository.findById(id);
            if (userFromDb.isPresent()) {
                User user = userFromDb.get();
                if (BooleanUtils.isTrue(user.getActivated()) && BooleanUtils.isFalse(user.getDeleted())) {
                    user.setDeleted(Boolean.TRUE);
                    user = userRepository.save(user);
                    return ResponseDto.builder()
                            .success(Boolean.TRUE)
                            .data(user.getId())
                            .httpStatus(HttpStatus.OK.value())
                            .build();
                }
                return ResponseDto.builder()
                        .success(Boolean.FALSE)
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .errMessage(Errors.ERR_ACCESS_DENIED)
                        .build();
            }
            return ResponseDto.builder()
                    .success(Boolean.FALSE)
                    .httpStatus(HttpStatus.NOT_FOUND.value())
                    .errMessage(Errors.NO_USER)
                    .build();
        }
        userFromDb = userRepository.findByEmail(SecurityUtils.getCurrentUserLoginOrThrow());
        User user = userFromDb.get();
        user.setDeleted(Boolean.TRUE);
        user = userRepository.save(user);
        return ResponseDto.builder()
                .httpStatus(HttpStatus.OK.value())
                .success(Boolean.TRUE)
                .data(user.getId())
                .build();
    }
}
