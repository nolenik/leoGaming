Чтобы правильно запустить проект нужно:
1. Скачать его с github
2. Открыть консоль и поменять директорию на *путь к проекту*\target. Пример для Windows:
cd C:\Users\Юра\Documents\Java\LeoGaming\target
3. Запустить исполняемый jar и в качестве параметров передать param1 - путь к проекту, param2 - файл который нужно 
отправить на сервер. В директории res находится 3 файла для примера: verify.xml, payment.xml, status.xml. Пример:
java -jar LeoGaming-0.0.1-SNAPSHOT.jar C:\Users\Юра\Documents\Java\LeoGaming verify.xml
