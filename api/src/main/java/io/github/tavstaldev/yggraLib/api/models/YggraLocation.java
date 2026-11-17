package io.github.tavstaldev.yggraLib.api.models;

/**
 * Stores a simple location with world name, coordinates, and rotation.
 * This class is used as a platform-independent location model.
 */
public class YggraLocation {
    private final String _worldName;
    private final double _x, _y, _z;
    private final float _yaw, _pitch;

    /**
     * Creates a new location.
     *
     * @param worldName the world name
     * @param x         the X coordinate
     * @param y         the Y coordinate
     * @param z         the Z coordinate
     * @param yaw       the yaw rotation
     * @param pitch     the pitch rotation
     */
    public YggraLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this._worldName = worldName;
        this._x = x;
        this._y = y;
        this._z = z;
        this._yaw = yaw;
        this._pitch = pitch;
    }

    /**
     * Returns the world name of this location.
     *
     * @return the world name
     */
    public String getWorldName() {
        return _worldName;
    }

    /**
     * Returns the X coordinate.
     *
     * @return the X coordinate
     */
    public double getX() {
        return _x;
    }

    /**
     * Returns the Y coordinate.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return _y;
    }

    /**
     * Returns the Z coordinate.
     *
     * @return the Z coordinate
     */
    public double getZ() {
        return _z;
    }

    /**
     * Returns the yaw rotation.
     *
     * @return the yaw value
     */
    public float getYaw() {
        return _yaw;
    }

    /**
     * Returns the pitch rotation.
     *
     * @return the pitch value
     */
    public float getPitch() {
        return _pitch;
    }
}
