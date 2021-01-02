package ir.smartdevelopers.smartfilebrowser.models;

import java.util.Objects;

public class AlbumModel {
    private long id;
    private String name;
    private String imagePath;
    private long timeTaken;

    public AlbumModel(long id, String name, String imagePath, long timeTaken) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.timeTaken = timeTaken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumModel that = (AlbumModel) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
}
