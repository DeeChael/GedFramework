package net.deechael.framework.response;

public abstract class ResponseListener {

    protected ResponseListener() {
    }

    public abstract void onResponse(PreparedResponse response);

    public boolean ignoreCancelled() {

        return true;
    }

}
