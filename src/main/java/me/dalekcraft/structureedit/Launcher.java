package me.dalekcraft.structureedit;

public final class Launcher {

    private Launcher() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        StructureEditApplication.launch(StructureEditApplication.class, args);
    }
}
