package md.frolov.legume.client.elastic.api;

import md.frolov.legume.client.elastic.model.reply.PingReply;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public class PingResponse extends ESResponse<PingReply>
{
    private final PingReply pingReply;

    public PingResponse(final PingReply pingReply)
    {
        this.pingReply = pingReply;
    }

    public PingReply getPingReply()
    {
        return pingReply;
    }
}
