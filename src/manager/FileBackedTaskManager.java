package manager;

import alltasks.Epic;
import alltasks.Subtask;
import alltasks.Task;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(IHistoryManager historyManager) {
        super(historyManager);
        this.file = new File(createFile("restore.csv"));
    }

    public FileBackedTaskManager(IHistoryManager historyManager, File tempFile) throws IOException {
        super(historyManager);
        this.file = tempFile;
    }

    private void save() {
        List<Task> allTasks = getAllTasks();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic");
            for (Task task : allTasks) {
                bufferedWriter.write(taskToString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());
        return allTasks;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return super.getEpicSubtasks(epicId);
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    public File getFile() {
        return file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        if (file.length() == 0) {
           return new FileBackedTaskManager(new InMemoryHistoryManager());
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
            List<Task> allTasks = new ArrayList<>();
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                allTasks.add(taskFromString(bufferedReader.readLine()));
            }
            for (Task task : allTasks) {
                switch (task.getTypeOfTask()) {
                    case TASK -> fileBackedTaskManager.addNewTask(task);
                    case EPIC -> fileBackedTaskManager.addNewEpic((Epic) task);
                    case SUBTASK -> fileBackedTaskManager.addNewSubtask((Subtask) task);
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private static String createFile(String fileName) throws ManagerSaveException {
        Path path = Path.of(fileName);
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            return fileName;
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }


    private String taskToString(Task task) {
        String mainEpic;
        if (task.isSubtask()) {
            mainEpic = String.valueOf(getSubtask(task.getId()).getEpicId());
        } else {
            mainEpic = "";
        }
        return "\n" + task.getId() + "," + task.getTypeOfTask().toString() + "," +
                task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," +
                mainEpic + " ".trim();
    }

    private static Task taskFromString(String value) {

        String[] array = value.trim().split(",");
        if (array[1].equals(String.valueOf(TypeOfTask.TASK))) {
            return new Task(array[2], array[4], Integer.parseInt(array[0]), array[3]);
        } else if (array[1].equals(String.valueOf(TypeOfTask.EPIC))) {
            return new Epic(array[2], array[4], Integer.parseInt(array[0]));
        } else {
            return new Subtask(array[2], array[4], Integer.parseInt(array[0]), array[3],
                    Integer.parseInt(array[5]));
        }
    }
}

