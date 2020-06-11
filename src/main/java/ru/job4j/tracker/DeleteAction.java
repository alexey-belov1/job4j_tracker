package ru.job4j.tracker;

public class DeleteAction implements UserAction {
    @Override
    public String name() {
        return "Delete item";
    }

    @Override
    public boolean execute(Input input, ITracker tracker) {
        String id = input.askStr("Enter id selected item: ");
        if (tracker.delete(id)) {
            System.out.println("Delete completed successfully!");
        } else {
            System.out.println("Delete was failed!");
        }
        return true;
    }
}
