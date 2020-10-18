package org.rocketproplab.marginalstability.flightcomputer.subsystems;

import org.rocketproplab.marginalstability.flightcomputer.comm.FramedPacketProcessor;
import org.rocketproplab.marginalstability.flightcomputer.comm.FramedSCM;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;
import org.rocketproplab.marginalstability.flightcomputer.looper.Looper;

/**
 * A subsystem that listens for SCMPacket and FrameSCM
 * to schedule commands.
 *
 * @author Chi Chow
 */
public class ScheduleCommandSubsystem implements Subsystem,
        PacketListener<SCMPacket>, FramedSCM.MessageCompletedListener {
  private Looper              looper;
  private OnFramedSCMListener onFramedSCMListener;

  private OnSCMPacketListener onSCMPacketListener;

  public ScheduleCommandSubsystem(FramedSCM framedSCM) {
    framedSCM.setMessageCompletedListener(this);
    this.onFramedSCMListener = null;
    this.onSCMPacketListener = null;
  }

  @Override
  public void prepare(Looper looper) {
    this.looper = looper;
  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {
    if (this.onSCMPacketListener != null) {
      this.onSCMPacketListener.onSCMPacket(looper, direction, packet);
    }
  }

  @Override
  public void onMessageCompleted(String message) {
    if (this.onFramedSCMListener != null) {
      this.onFramedSCMListener.onFramedSCM(looper, message);
    }
  }

  public void setOnFramedSCMListener(OnFramedSCMListener onFramedSCMListener) {
    this.onFramedSCMListener = onFramedSCMListener;
  }

  public void setOnSCMPacketListener(OnSCMPacketListener onSCMPacketListener) {
    this.onSCMPacketListener = onSCMPacketListener;
  }

  @FunctionalInterface
  public interface OnFramedSCMListener {

    public void onFramedSCM(Looper scheduler, String message);
  }

  @FunctionalInterface
  public interface OnSCMPacketListener {
    public void onSCMPacket(Looper scheduler, PacketDirection direction, SCMPacket packet);
  }
}
