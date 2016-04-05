package name.heqian.cs528.googlefit;

public class ActivityInfo {
    private int _id;
    private String _timeStamp;
    private ActivityType _type;

    public ActivityInfo () {
        //default constructor
    }

    public ActivityInfo(ActivityType type, String timeStamp) {
        this._type = type;
        this._timeStamp = timeStamp;
    }

    public void set_timeStamp(String _timeStamp) {
        this._timeStamp = _timeStamp;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_type(ActivityType _type) {
        this._type = _type;
    }

    public int get_id() {
        return _id;
    }

    public String get_timeStamp() {
        return _timeStamp;
    }

    public ActivityType get_type() {
        return _type;
    }
}
