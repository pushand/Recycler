package pw.pushan.recycler;

/**
 * Created by pushan on 14/07/17.
 */

public abstract class RecyclerBaseModel extends BaseModel {

    protected ListApi listApi;

    /**
     * @param listApi api that holds the applist
     * */
    public RecyclerBaseModel(ListApi listApi, int identifier) {
        super(identifier);
        this.listApi = listApi;
    }

    public ListApi getListApi() {
        return listApi;
    }

    public void remove() {
        listApi.getAppList().remove(this);
    }

    public void changed(String payLoad) {
        listApi.getAppList().changed(this, payLoad);
    }

    public int indexOf() {
        return listApi.getAppList().indexOf(this);
    }

}
