## Java Core: Игра Крестики-Нолики

### Задача 2 к Уроку 5

Предположить, что числа в исходном массиве из 9 элементов имеют диапазон[0, 3], и представляют собой, например, состояния ячеек поля для игры в крестикинолики, где 0 – это пустое поле, 1 – это поле с крестиком, 2 – это поле с ноликом, 3 – резервное значение. Такое предположение позволит хранить в одном числе типа int всё поле 3х3. Записать в файл состояние поля и добавить возможность восстановить состояние поля из файла (*) сделать доп возможность в игре крестики-нолики - метод для сохранения состояния игры и восстановление из файла.

### Решение

Реализацию побитовой конвертации в int и сохрания состояния игрового поля см.
в классе `GameSaver` &mdash; `src/edu/alexey/tictactoegame/GameSaver.java`

### Комментарии к исходному заданию

### Компиляция и запуск

	javac -sourcepath src -d bin src/edu/alexey/tictactoegame/App.java

	java -classpath bin edu.alexey.tictactoegame.App

### Пример работы приложения

*Пример с расширенным размером поля*

![Снимок экрана от 2023-07-07 17-39-05](https://github.com/alexeycoder/java-core-tic-tac-toe/assets/109767480/024a5158-8d52-4ee9-a988-aa6353b7c20d)

![Снимок экрана от 2023-07-07 17-39-49](https://github.com/alexeycoder/java-core-tic-tac-toe/assets/109767480/a70aadaf-9a05-4690-ba6d-741f006937f0)

*Игра с нормальным размером поля*

![Снимок экрана от 2023-07-07 17-34-36](https://github.com/alexeycoder/java-core-tic-tac-toe/assets/109767480/3135de26-179f-4019-a150-15f4ca15536d)

![Снимок экрана от 2023-07-07 17-34-56](https://github.com/alexeycoder/java-core-tic-tac-toe/assets/109767480/9bebd4e9-42e1-4eb2-b4e6-189bea0adf81)

![Снимок экрана от 2023-07-07 17-35-13](https://github.com/alexeycoder/java-core-tic-tac-toe/assets/109767480/99022a9f-7056-4e56-b521-714b2538941f)
