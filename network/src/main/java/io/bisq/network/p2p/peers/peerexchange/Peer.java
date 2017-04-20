package io.bisq.network.p2p.peers.peerexchange;

import io.bisq.common.Payload;
import io.bisq.common.app.Version;
import io.bisq.common.persistence.Persistable;
import io.bisq.generated.protobuffer.PB;
import io.bisq.network.p2p.NodeAddress;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;

@ToString
@Slf4j
public final class Peer implements Payload, Persistable {
    // That object is sent over the wire, so we need to take care of version compatibility.
    private static final long serialVersionUID = Version.P2P_NETWORK_VERSION;
    private static final int MAX_FAILED_CONNECTION_ATTEMPTS = 5;

    // Payload
    public final NodeAddress nodeAddress;
    public final Date date;

    // Domain
    transient private int failedConnectionAttempts = 0;

    public Peer(NodeAddress nodeAddress) {
        this(nodeAddress, new Date());
    }

    public Peer(NodeAddress nodeAddress, Date date) {
        this.nodeAddress = nodeAddress;
        this.date = date;
    }

    public void increaseFailedConnectionAttempts() {
        this.failedConnectionAttempts++;
    }

    public boolean tooManyFailedConnectionAttempts() {
        return failedConnectionAttempts >= MAX_FAILED_CONNECTION_ATTEMPTS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Peer)) return false;

        Peer that = (Peer) o;

        return !(nodeAddress != null ? !nodeAddress.equals(that.nodeAddress) : that.nodeAddress != null);

    }

    // We don't use the lastActivityDate for identity
    @Override
    public int hashCode() {
        return nodeAddress != null ? nodeAddress.hashCode() : 0;
    }

    @Override
    public PB.Peer toProto() {
        return PB.Peer.newBuilder().setNodeAddress(nodeAddress.toProto())
                .setDate(date.getTime()).build();
    }

    public static Peer fromProto(PB.Peer peer) {
        return new Peer(NodeAddress.fromProto(peer.getNodeAddress()), Date.from(Instant.ofEpochMilli(peer.getDate())));
    }
}
