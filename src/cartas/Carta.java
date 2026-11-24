package cartas;

public abstract class Carta {
    // atributos
    private int numero;
    private String texto;

    //constructores
    public Carta(int n, String t){
        this.numero = n;
        this.texto = t;
    }

    //getters

    public int getNumero() {
        return numero;
    }
    public String getTexto(){
        return texto;
    }


    //metodos

    public abstract void accion();
}
