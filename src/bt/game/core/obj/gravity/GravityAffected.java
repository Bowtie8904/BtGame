package bt.game.core.obj.gravity;

public interface GravityAffected
{
    public void setVelocityY(double velocity);

    public double getVelocityY();

    public double getMaxGravityVelocity();

    public double getGravityVelocityGain();

    public void setGravityVelocityGain(double gain);
}