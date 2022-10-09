package kz.kalybayevv.VtbNews.services;

import kz.kalybayevv.VtbNews.web.dto.ResponseDto;

import java.util.concurrent.Future;

public interface MailService {
    Future<Boolean> sendEmail(String to, String subject, String content);
}
