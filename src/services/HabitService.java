package services;

import models.Habit;
import models.Person;
import repositories.PeopleRepository;
import java.util.*;
import java.util.stream.Collectors;

public class HabitService {
    private final PeopleRepository peopleRepository;
    private static final Scanner scanner = new Scanner(System.in);
    private List<Habit> habits;

    public HabitService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public void viewHabits(Person currentPerson) {
        habits = currentPerson.getHabits();
        outer:
        while(true) {

            if (habits.isEmpty()) {
                System.out.println("\nПривычки отсутствуют.");
            } else {
                System.out.println("\n--- Ваши привычки ---");
                for (Habit habit : habits) {
                    System.out.println("Название: " + habit.getName() + "\nОписание: " + habit.getDescription() + "\nДата создания: " + habit.getCreateAt());
                    System.out.println("---------------");
                }
            }

            System.out.println("\n--- Управление ---");
            System.out.println("1. Добавить привычку");
            System.out.println("2. Посмотреь статистику по привычке");
            System.out.println("3. Удалить привычку");
            System.out.println("4. Редактировать привычку");
            System.out.println("5. Отметить выполнение привычки");
            System.out.println("6. Показать статистику привычек за неделю");
            System.out.println("7. Показать текущую серию выполнения привычек");
            System.out.println("8. Фильтрация привычек по дате создания");
            System.out.println("9. Фильтрация привычек по статусу выполнения");
            System.out.println("10. Вернуться");

            System.out.print("Выберите действие (1-10): ");

            String choice = scanner.nextLine().trim();

            switch(choice) {
                case "1":
                    addHabit(currentPerson);
                    break;
                case "2":
                    break;
                case "3":
                    removeHabit(currentPerson);
                    break;
                case "4":
                    editHabit(currentPerson);
                    break;
                case "5":
                    completeHabit(currentPerson);
                    break;
                case "6":
                    generateHabitStats(currentPerson, 7);
                    break;
                case "7":
                    calculateStreak(currentPerson);
                    break;
                case "8":
                    filterHabitsByCreationDate(currentPerson);
                    break;
                case "9":
                    filterHabitsByCompletionStatus(currentPerson);
                    break;
                case "10":
                    break outer;
                default:
                    System.out.println("Неверный выбор. Пожалуйста повторите попытку.");
            }
        }
    }

    public void addHabit(Person person) {
        System.out.print("Введите название новой привычки или exit если передумали: ");
        String name = scanner.nextLine().trim();
        if(name.equals("exit"))
            return;

        System.out.print("Введите описание привычки или exit если передумали: ");
        String description = scanner.nextLine().trim();
        if(description.equals("exit"))
            return;

        boolean status = true;
        String frequency = null;
        while(status) {
            System.out.println("Выберите частоту выполнения привычки:");
            System.out.println("1. Ежедневно");
            System.out.println("2. Еженедельно");
            System.out.println("Введите 1 или 2: ");

             frequency = scanner.nextLine().trim();

            switch(frequency) {
                case "1":
                    status = false;
                    frequency = "Ежедневно";
                    break;
                case "2":
                    status = false;
                    frequency = "Еженедельно";
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста повторите попытку.");

            }

        }

        Habit habit = new Habit(name, description, frequency, person);
        person.addHabit(habit);

        System.out.println("Привычка успешно добавлена!");
        peopleRepository.saveData();  // Сохраняем изменения в базе данных
    }

    public void editHabit(Person person) {
        boolean status = true;
        while(status) {
            System.out.println("\n--- РЕДАКТОР ПРИВЫЧЕК: ---");
            System.out.println("--- Ваши привычки ---");
            for (int i = 0; i < habits.size(); i++)
                System.out.println((i + 1) + ". " + habits.get(i).getName());

            System.out.println("Введите номер привычки для редактирования (1-" + habits.size()+ ") или exit если передумали: ");

            String choice = scanner.nextLine().trim();
            if(choice.equals("exit"))
                return;

            int intChoice = Integer.parseInt(choice);

            if (intChoice < 1 || intChoice > habits.size())
                System.out.println("Неверный выбор, попробуйте еще!");

            Habit habitToEdit = habits.get(intChoice - 1);

            if(!showEditMenu(habitToEdit))
                continue;


        }
    }

    private boolean showEditMenu(Habit habit) {

        while(true) {
            System.out.println("\nИнформация о привычке:");
            System.out.println("1. Название привычки: " + habit.getName());
            System.out.println("2. Описание привычки: " + habit.getDescription());
            System.out.println("3. Частота выполнения: " + habit.getFrequency());

            System.out.println("Введите номер параметра для редактирования (1-3) или exit если передумали: ");
            String choice = scanner.nextLine().trim();

            if(choice.equals("exit"))
                return false;

            switch(choice) {
                case "1":
                    editName(habit);
                    break;
                case "2":
                    editDescription(habit);
                    break;
                case "3":
                    editFrequency(habit);
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста повторите попытку.");
            }


        }
    }

    public void editName(Habit habit) {
        System.out.println("Введите новое название:");
        String name = scanner.nextLine().trim();
        habit.setName(name);
        System.out.println("Вы успешно изменили название!");
    }

    public void editDescription(Habit habit) {
        System.out.println("Введите новое описание:");
        String description = scanner.nextLine().trim();
        habit.setDescription(description);
        System.out.println("Вы успешно изменили описание!");
    }

    public void editFrequency(Habit habit) {
        boolean status = true;
        String frequency = null;
        while(status) {
            System.out.println("Выберите частоту выполнения привычки:");
            System.out.println("1. Ежедневно");
            System.out.println("2. Еженедельно");
            System.out.println("Введите 1 или 2: ");

            frequency = scanner.nextLine().trim();

            switch(frequency) {
                case "1":
                    status = false;
                    habit.setFrequency("Ежедневно");
                    break;
                case "2":
                    status = false;
                    habit.setFrequency("Еженедельно");
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста повторите попытку.");

            }
            System.out.println("Вы успешно изменили частоту привычки!");

        }
    }

    public void removeHabit(Person person) {
        List<Habit> habits = person.getHabits();

        if (habits.isEmpty()) {
            System.out.println("Нет привычек для удаления.");
            return;
        }

        System.out.println("Выберите привычку для удаления: ");
        for (int i = 0; i < habits.size(); i++) {
            System.out.println((i + 1) + ". " + habits.get(i).getName());
        }

        System.out.println("Введите номер привычки (1-" + habits.size()+ ") или exit если передумали: ");
        String choice = scanner.nextLine().trim();

        if(choice.equals("exit"))
            return;

        int intChoice = Integer.parseInt(choice);

        if (intChoice < 1 || intChoice > habits.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        Habit habitToRemove = habits.get(intChoice - 1);
        person.removeHabit(habitToRemove);
        System.out.println("Привычка успешно удалена!");
        peopleRepository.saveData();  // Сохраняем изменения в базе данных
    }

    // Метод для отметки выполнения привычки
    public void completeHabit(Person person) {
        List<Habit> habit = person.getHabits();

        if(habits.isEmpty()) {
            System.out.println("Нет привычек для выполнения.");
            return;
        }

        System.out.println("Выберите привчку для отметки выполнения: ");

        for(int i = 0; i < habit.size(); i++)
            System.out.println((i+1) + ". " +  habits.get(i).getName());


        int choice = scanner.nextInt();
        if(choice < 1 || choice > habits.size()) {
            System.out.println("Неверный выбор!");
            return;
        }

        Habit habitToComplete = habits.get(choice - 1);
        habitToComplete.addCompletionDate(new Date());
        System.out.println("Привычка \"" + habitToComplete.getName() + "\" отмечена как выполненная!");
        peopleRepository.saveData();  // Сохраняем изменения
    }

    // Генерация статистики выполнения за указанный период (например, за последние 7 дней)
    public void generateHabitStats(Person person, int days) {
        List<Habit> habits = person.getHabits();

        if (habits.isEmpty()) {
            System.out.println("Нет привычек для анализа.");
            return;
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        Date startDate = calendar.getTime();

        System.out.println("\n--- Статистика выполнения привычек за последние " + days + " дней ---");
        for (Habit habit : habits) {
            long count = habit.getCompletionDates().stream()
                    .filter(date -> date.after(startDate))
                    .count();
            System.out.println("Привычка: " + habit.getName() + " - выполнено " + count + " раз(а)");
        }
    }

    // Подсчет текущей серии выполнения (streak) для привычки
    public void calculateStreak(Person person) {
        List<Habit> habits = person.getHabits();

        if (habits.isEmpty()) {
            System.out.println("Нет привычек для анализа.");
            return;
        }

        System.out.println("\n--- Текущие серии выполнения привычек ---");
        for (Habit habit : habits) {
            List<Date> completionDates = habit.getCompletionDates().stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            if (completionDates.isEmpty()) {
                System.out.println("Привычка: " + habit.getName() + " - нет данных по выполнению.");
                continue;
            }

            int streak = 1;  // Минимальная серия - 1 день
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            for (int i = 1; i < completionDates.size(); i++) {
                calendar.setTime(completionDates.get(i - 1));
                calendar.add(Calendar.DAY_OF_YEAR, -1);  // Ожидаем, что выполнение на следующий день

                if (!calendar.getTime().equals(completionDates.get(i))) {
                    break;  // Если нет выполнения на следующий день, серия прерывается
                }

                streak++;
            }

            System.out.println("Привычка: " + habit.getName() + " - текущая серия выполнения: " + streak + " день(дней)");
        }
    }

    // Фильтрация по дате создания
    public void filterHabitsByCreationDate(Person person) {
        List<Habit> habits = person.getHabits();

        if (habits.isEmpty()) {
            System.out.println("Нет привычек для фильтрации.");
            return;
        }

        System.out.println("Фильтрация привычек по дате создания (новые привычки):");
        habits.stream()
                .sorted(Comparator.comparing(Habit::getCreateAt).reversed())  // Сортировка по дате
                .forEach(habit -> System.out.println(habit.getName() + " (дата создания: " + habit.getCreateAt() + ")"));
    }

    // Фильтрация по статусу выполнения
    public void filterHabitsByCompletionStatus(Person person) {
        List<Habit> habits = person.getHabits();

        if (habits.isEmpty()) {
            System.out.println("Нет привычек для фильтрации.");
            return;
        }

        System.out.println("Фильтрация привычек по статусу выполнения:");
        habits.stream()
                .filter(habit -> !habit.getCompletionDates().isEmpty())  // Только выполненные привычки
                .forEach(habit -> System.out.println(habit.getName() + " (выполнено " + habit.getCompletionDates().size() + " раз(а))"));
    }

}


