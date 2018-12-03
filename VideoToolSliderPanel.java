import javax.swing.*;
import java.awt.*;

public class VideoToolSliderPanel extends JPanel {
    private JTextField sliderFrameField;
    private JSlider slider;
    private char id;

    public VideoToolSliderPanel(char id) {
        this.id = id;
        this.setBackground(Color.LIGHT_GRAY);

        this.sliderFrameField = new JTextField();
        this.sliderFrameField.setText("0001");
    }

    public char getId() {
        return id;
    }

    public void setSlider(JSlider slider) {
        this.slider = slider;
    }

    public JSlider getSlider() {
        return slider;
    }

    public void setSliderFrameField(JTextField sliderFrameField) {
        this.sliderFrameField = sliderFrameField;
    }

    public JTextField getSliderFrameField() {
        return sliderFrameField;
    }
}
