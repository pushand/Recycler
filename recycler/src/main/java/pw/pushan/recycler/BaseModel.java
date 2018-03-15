package pw.pushan.recycler;

/**
 * Created by pushan on 12/07/17.
 */

public abstract class BaseModel implements ViewModel {

    protected int identifier;

    protected BaseModel(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
    }
}
