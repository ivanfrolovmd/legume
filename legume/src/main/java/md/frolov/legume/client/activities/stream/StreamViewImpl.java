package md.frolov.legume.client.activities.stream;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StreamViewImpl extends Composite implements StreamView {

    private static StreamViewImplUiBinder uiBinder = GWT.create(StreamViewImplUiBinder.class);

    interface StreamViewImplUiBinder extends UiBinder<Widget, StreamViewImpl> {
    }

    public StreamViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
