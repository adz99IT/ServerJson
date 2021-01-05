public class Start {
    public static void main(String[] args){
        Controller c = new Controller();
        Model m = new Model(true, c);
        c.setModel(m);

    }
}
