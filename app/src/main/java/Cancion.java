public class Cancion {

    private String id;
    private String title;
    private String artist;

    public Cancion (String id, String title, String artist){
        setId(id);
        setTitle(title);
        setArtist(artist);
    }

    public void setId(String id){
        this.id = id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getArtist(){
        return this.artist;
    }


}
