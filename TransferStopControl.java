
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class TransferStopControl {

    private final JFrame frame;
    private final JButton stopButton;
    private final JLabel statusLabel;

    private final AtomicReference<Socket> activeSocket;
    private final AtomicBoolean cancelled;

    public TransferStopControl(
            String title,
            String buttonText,
            AtomicReference<Socket> activeSocket,
            AtomicBoolean cancelled) {

        this.activeSocket = activeSocket;
        this.cancelled = cancelled;

        frame = new JFrame(title);
        frame.setLayout(new FlowLayout());

        statusLabel = new JLabel("Waiting for transfer...");
        stopButton = new JButton(buttonText);

        stopButton.setEnabled(false);

        frame.add(statusLabel);
        frame.add(stopButton);

        stopButton.addActionListener(e -> {

            if (!cancelled.compareAndSet(false, true)) {
                return;
            }

            statusLabel.setText("Stopping transfer...");
            stopButton.setEnabled(false);

            Socket socket = activeSocket.get();

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.println(
                        "Error closing connection: " + ex.getMessage()
                    );
                }
            }
        });

        frame.setSize(330, 120);
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setActive(boolean active) {

        SwingUtilities.invokeLater(() -> {

            stopButton.setEnabled(active);

            if (active) {
                statusLabel.setText("Transfer in progress...");
            } else {
                statusLabel.setText("Waiting for transfer...");
            }
        });
    }

    public void close() {

        SwingUtilities.invokeLater(() -> frame.dispose());
    }
}
