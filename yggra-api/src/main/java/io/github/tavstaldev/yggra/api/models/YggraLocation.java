package io.github.tavstaldev.yggra.api.models;

public class YggraLocation {
    private final String _worldName;
    private final double _x, _y, _z;
    private final float _yaw, _pitch;

    public YggraLocation(String _worldName, double _x, double _y, double _z, float _yaw, float _pitch) {
        this._worldName = _worldName;
        this._x = _x;
        this._y = _y;
        this._z = _z;
        this._yaw = _yaw;
        this._pitch = _pitch;
    }

    public String getWorldName() {
        return _worldName;
    }

    public double getX() {
        return _x;
    }

    public double getY() {
        return _y;
    }

    public double getZ()
    {
        return _z;
    }

    public float getYaw()
    {
        return _yaw;
    }

    public float getPitch() {
        return _pitch;
    }
}
