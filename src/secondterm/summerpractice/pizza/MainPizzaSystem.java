package secondterm.summerpractice.pizza;

import secondterm.summerpractice.pizza.users.Admin;
import secondterm.summerpractice.pizza.users.Client;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class MainPizzaSystem {
    private static final int TIME_TO_SLEEP = 50;
    private static final String INTEGER_REGEX = "^[\\d]+$";
    private static final String REPEAT_OR_END = "[rep] - Повторить попытку / [end] - Выйти";
    static HashMap<String, Client> users = new HashMap<>();
    static HashMap<String, Admin> admins = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static City city = new City();
    private static final String YES = "yes";
    private static final String NO = "no";
    private static final String FIRST_FLAG = "-f";
    private static final String SECOND_FLAG = "--file";
    private static final String END = "end";
    private static final String REGISTRATION = "reg";
    private static final String ENTER = "ent";
    private static final String ADMIN_MODE = "am";
    private static final String REPEAT = "rep";
    private static final int NUMBER_OF_ARGS = 2;
    private static final int ONE = 1;
    static LinkedList<String> commandsAndData = new LinkedList<>();
    static Deque<String> commandsFromFile = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        admins.put("admin", new Admin("Alex P", "admin", "admin"));
        admins.put("artyom", new Admin("Artyom", "artyom", "artyom"));
        readCommandsFromFile(args);
        mainMenu();
    }

    private static void mainMenu() throws IOException {
        String choose = "";
        while (!choose.equals(END)) {
            System.out.println("[reg] - Регистрация");
            System.out.println("[ent] - Вход");
            System.out.println("[am] - Administrator Mode");
            System.out.println("[end] - Выйти из программы");
            choose = getStringValue().toLowerCase();
            if (!choose.equals(END)) {
                commandsAndData.add(choose);
            }
            switch (choose) {
                case REGISTRATION -> {
                    System.out.println("Введите имя: ");
                    String nickname = getStringValue();
                    commandsAndData.add(nickname);
                    System.out.println("Введите логин: ");
                    String login = getStringValue();
                    commandsAndData.add(login);
                    System.out.println("Введите пароль: ");
                    String password = getStringValue();
                    commandsAndData.add(password);
                    StringBuilder bufferDistricts = new StringBuilder();
                    for (int i = 0; i < City.nameDistricts.size(); i++) {
                        bufferDistricts.append(i + 1).append(") ").append(City.nameDistricts.get(i)).append("\n");
                    }
                    System.out.print(bufferDistricts);
                    String districtString = null;
                    int realDistrictNumber = 0;
                    int district = 0;
                    do {
                        System.out.println("Выберите номер вашего района: ");
                        String tempDistrict = getStringValue();
                        if (tempDistrict.equalsIgnoreCase(END)) {
                            break;
                        } else if (tempDistrict.matches(INTEGER_REGEX)) {
                            district = Integer.parseInt(tempDistrict);
                        }
                        districtString = tempDistrict;
                        realDistrictNumber = district - ONE;
                    } while (realDistrictNumber < 0
                            || realDistrictNumber >= City.nameDistricts.size());
                    commandsAndData.add(districtString);
                    users.put(login, new Client(nickname, login, password, realDistrictNumber));
                }
                case ENTER -> {
                    System.out.println("Введите логин: ");
                    String takeLogin = getStringValue();
                    commandsAndData.add(takeLogin);
                    if (users.containsKey(takeLogin)) {
                        boolean correctPassword;
                        do {
                            System.out.println("Введите пароль: ");
                            String takePassword = getStringValue();
                            commandsAndData.add(takePassword);
                            if (users.get(takeLogin).enter(takeLogin, takePassword)) {
                                correctPassword = false;
                                System.out.println("Вход выполнен!");
                                System.out.println("Добрый день, " + users.get(takeLogin).getNickname() + "!");
                                System.out.println("Хотите заказать пиццу? [yes] - Да / [no] - Нет");
                                boolean isYesOrNo;
                                do {
                                    String takeCommand = getStringValue().toLowerCase();
                                    commandsAndData.add(takeCommand);
                                    if (takeCommand.equals(YES)) {
                                        city.makeRoad(users.get(takeLogin).getDist());
                                        isYesOrNo = false;
                                    } else if (takeCommand.equals(NO)) {
                                        System.out.println("Заказ пиццы отменён.");
                                        isYesOrNo = false;
                                    } else {
                                        System.out.println("Неизвестый выбор, повторите попытку.");
                                        isYesOrNo = true;
                                    }
                                } while (isYesOrNo);
                            } else {
                                System.out.println("Неверный пароль.");
                                correctPassword = isCorrectPassword();
                            }
                        } while (correctPassword);
                    } else {
                        System.out.println("Такого логина нет в базе.");
                    }
                }
                case ADMIN_MODE -> {
                    System.out.println("Вы пытаетесь зайти в режим администратора!");
                    System.out.println("Введите логин: ");
                    String takeLogin = getStringValue();
                    commandsAndData.add(takeLogin);
                    if (admins.containsKey(takeLogin)) {
                        boolean correctPassword;
                        do {
                            System.out.println("Введите пароль: ");
                            String takePassword = getStringValue();
                            commandsAndData.add(takePassword);
                            if (admins.get(takeLogin).enter(takeLogin, takePassword)) {
                                correctPassword = false;
                                do {
                                    System.out.println("Хотите добавить новый район для доставки? [yes] - Да / [no] - Нет");
                                    String addVertex = getStringValue().toLowerCase();
                                    commandsAndData.add(addVertex);
                                    if (addVertex.equals(NO)) {
                                        break;
                                    }
                                    if (addVertex.equals(YES)) {
                                        boolean regionIsExists = true;
                                        String nameVertex;
                                        do {
                                            System.out.println("Введите название района: ");
                                            nameVertex = getStringValue();
                                            if (nameVertex.equalsIgnoreCase(END)) {
                                                System.out.println("Добавление нового района прервано!");
                                                break;
                                            }
                                            for (String obj : City.nameDistricts) {
                                                if (Objects.equals(obj.toLowerCase(), nameVertex.toLowerCase())) {
                                                    System.out.println("Район с таким названием уже существует, "
                                                            + "повторите попытку.");
                                                    regionIsExists = true;
                                                    break;
                                                } else {
                                                    regionIsExists = false;
                                                }
                                            }
                                        } while (regionIsExists);
                                        if (nameVertex.equalsIgnoreCase(END)) {
                                            break;
                                        } else {
                                            commandsAndData.add(nameVertex);
                                        }
                                        StringBuilder bufferRegions = new StringBuilder();
                                        for (int i = 0; i < City.nameDistricts.size(); i++) {
                                            bufferRegions.append(i + 1)
                                                         .append(") ")
                                                         .append(City.nameDistricts.get(i))
                                                         .append("\n");
                                        }
                                        System.out.print(bufferRegions);
                                        boolean addVertexFlag;
                                        LinkedList<Integer> connectsVertex = new LinkedList<>();
                                        LinkedList<Integer> connectsValues = new LinkedList<>();
                                        do {
                                            int realDistrictNumber = 0;
                                            String districtString = null;
                                            int district = 0;
                                            do {
                                                System.out.println("Выберите номер района к которому есть путь из нового: ");
                                                String tempDistrict = getStringValue();
                                                if (tempDistrict.equalsIgnoreCase(END)) {
                                                    break;
                                                } else if (tempDistrict.matches(INTEGER_REGEX)) {
                                                    district = Integer.parseInt(tempDistrict);
                                                }
                                                districtString = tempDistrict;
                                                realDistrictNumber = district - ONE;
                                            } while (realDistrictNumber < 0
                                                    || realDistrictNumber >= City.nameDistricts.size());
                                            commandsAndData.add(districtString);
                                            int value = 0;
                                            String districtValue = null;
                                            do {
                                                System.out.println("Введите время дороги к этому району: ");
                                                String tempValue = getStringValue();
                                                if (tempValue.equalsIgnoreCase(END)) {
                                                    break;
                                                } else if (tempValue.matches(INTEGER_REGEX)) {
                                                    value = Integer.parseInt(tempValue);
                                                }
                                                districtValue = tempValue;
                                            } while (value <= 0);
                                            commandsAndData.add(districtValue);
                                            connectsVertex.add(realDistrictNumber);
                                            connectsValues.add(value);
                                            System.out.println("Хотите добавить ещё один путь? [yes] - Да / [no] - Нет");
                                            String addNewRegion = getStringValue().toLowerCase();
                                            commandsAndData.add(addNewRegion);
                                            addVertexFlag = addNewRegion.equals(YES);
                                        } while (addVertexFlag);
                                        city.addVertex(nameVertex, connectsVertex, connectsValues);
                                    } else {
                                        System.out.println("У администратора нет других возможностей :(");
                                    }
                                } while (true);
                            } else {
                                System.out.println("Неверный пароль!");
                                correctPassword = isCorrectPassword();
                            }
                        } while (correctPassword);
                    } else {
                        System.out.println("Администратора с таким логином нет.");
                    }
                }
                case END -> {
                    System.out.println("[yes] - Сохранить данные в файл");
                    System.out.println("[no] - Завершить работу программы без сохранения");
                    String save = getStringValue().toLowerCase();
                    if (save.equals(YES)) {
                        DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
                        File myFile = new File
                                ("result_" + timeStampPattern.format(java.time.LocalDateTime.now()) + ".txt");
                        FileWriter writer = null;
                        try {
                            writer = new FileWriter(myFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for (String strAdd : commandsAndData) {
                            assert writer != null;
                            writer.write(strAdd);
                            writer.write(System.lineSeparator());
                        }
                        assert writer != null;
                        writer.flush();
                        writer.close();
                    }
                }
                default -> System.out.println("Такой команды нет.");
            }
        }
        scanner.close();
    }

    private static boolean isCorrectPassword() {
        boolean correctPassword;
        System.out.println(REPEAT_OR_END);
        String commandPass = getStringValue().toLowerCase();
        commandsAndData.add(commandPass);
        if (commandPass.equals(REPEAT)) {
            correctPassword = true;
        } else if (commandPass.equals(END)) {
            correctPassword = false;
        } else {
            System.out.println("Неизвестная команда, завершаем работу.");
            correctPassword = false;
        }
        return correctPassword;
    }

    private static String getStringValue() {
        String command;
        if (!commandsFromFile.isEmpty()) {
            command = commandsFromFile.poll();
//            commandsAndData.add(command);
            System.out.println(command);

            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поймано исключение. Продолжаем работу");
            }
        } else {
            command = scanner.nextLine();
        }
        return command;
    }

    private static void readCommandsFromFile(String[] args) {
        if (args.length == NUMBER_OF_ARGS
                && (args[0].equalsIgnoreCase(FIRST_FLAG) || args[0].equalsIgnoreCase(SECOND_FLAG))) {
            try (BufferedReader bufReader = new BufferedReader(new FileReader(args[1]))) {
                String s;
                while ((s = bufReader.readLine()) != null) {
                    commandsFromFile.add(s);
                }
            } catch (IOException e) {
                System.out.println("Файл не найден. Проверьте правильность имени и повторите попытку.");
            }
        }
    }
}
