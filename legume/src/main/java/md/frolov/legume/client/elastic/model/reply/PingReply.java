package md.frolov.legume.client.elastic.model.reply;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface PingReply
{
    String getName();
    int getStatus();
    boolean isOk();
    String getTagline();

    Version getVersion();

    interface Version {
        String getNumber();
        @AutoBean.PropertyName("snapshot_build")
        boolean isSnapshotBuild();
    }
}
