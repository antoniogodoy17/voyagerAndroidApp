package voyager.voyager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Actividad implements Serializable {

    public HashMap<String, String> calificacion;
    public int costo;
    public HashMap<String, String> imagenes;
    public String categoria;
    public String descripcion;
    public String estado;
    public  String fecha;
    public String horario;
    public HashMap<String, String> resenas;
    public ArrayList<Object> tags;
    public String tipo;
    public String titulo;
    public HashMap<String , Object> ubicacion;

    public Actividad(){}

    public Actividad(HashMap<String, String> calificacion, int costo, HashMap<String, String> imagenes, String categoria, String descripcion, String estado,  String fecha, String horario, HashMap<String, String> resenas, ArrayList<Object> tags, String tipo, String titulo, HashMap<String, Object> ubicacion){
        this.calificacion = calificacion;
        this.costo = costo;
        this.imagenes = imagenes;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
        this.horario = horario;
        this.resenas = resenas;
        this.tags = tags;
        this.tipo = tipo;
        this.titulo = titulo;
        this.ubicacion = ubicacion;

    }

}
