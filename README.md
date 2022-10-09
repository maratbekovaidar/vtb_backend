Чтобы Запустить backend application.
Вам понадобиться jdk 11 java установленный postgresql + веб сервер redis.
Дальше вам нужно будет зайти в application.yml расположенный внутри директории resources.
поменять там password and username на postgresql который стоит на localhost у вас
,дальше создать database пустой в postgresql и поменять на созданный database в applcation.yml
поле выглядид так localhost:5432/yourdatabase.
после запуска проекта
запустить postgresql и выполнить эти команды
insert into roles values (1, 'COMMON', 'DSADAS', 'dsa', 'dsa');
insert into roles values (2, 'ACCOUNTANT', 'DSADAS', 'dsa', 'dsa');
insert into roles values (3, 'BUSINESS', 'DSADAS', 'dsa', 'dsa');

мы не делали миграцию так как не успели(.

если все успешно без ошибок запустилась. То это success!.