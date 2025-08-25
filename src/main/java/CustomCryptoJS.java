import burp.api.montoya.MontoyaApi;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class CustomCryptoJS {
    public static String aes_passphrase = "";
    public static String url_parameter = "";
    public static String body_parameter = "";
    public static String header = "";
    public static boolean checkbox1_selected = false;
    public static boolean checkbox2_selected = false;
    public static boolean checkbox3_selected = false;

    public static String encrypt(String plaintext, String pass) throws Exception {
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);
        byte[] keyIv = EVP_BytesToKey(pass.getBytes("UTF-8"), salt, 32, 16);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Arrays.copyOf(keyIv, 32), "AES"),
                new IvParameterSpec(Arrays.copyOfRange(keyIv,32,48)));
        byte[] encrypted = c.doFinal(plaintext.getBytes("UTF-8"));
        byte[] header = ("Salted__").getBytes("UTF-8");
        byte[] out = new byte[header.length + salt.length + encrypted.length];
        System.arraycopy(header,0,out,0,header.length);
        System.arraycopy(salt,0,out,header.length,salt.length);
        System.arraycopy(encrypted,0,out,header.length+salt.length,encrypted.length);
        return Base64.getEncoder().encodeToString(out);
    }

    public static String decrypt(String cipher64, String pass) throws Exception {
        byte[] data = Base64.getDecoder().decode(cipher64);
        if (!new String(data,0,8,"UTF-8").equals("Salted__"))
            throw new IllegalArgumentException("Bad header");
        byte[] salt = Arrays.copyOfRange(data,8,16);
        byte[] cipher = Arrays.copyOfRange(data,16,data.length);
        byte[] keyIv = EVP_BytesToKey(pass.getBytes("UTF-8"), salt, 32, 16);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Arrays.copyOf(keyIv,32),"AES"),
                new IvParameterSpec(Arrays.copyOfRange(keyIv,32,48)));
        return new String(c.doFinal(cipher),"UTF-8");
    }

    private static byte[] EVP_BytesToKey(byte[] pass, byte[] salt, int keyLen, int ivLen) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyIv = new byte[keyLen+ivLen];
        byte[] prev = new byte[0];
        int pos = 0;
        while (pos < keyIv.length) {
            md.reset();
            md.update(prev);
            md.update(pass);
            md.update(salt);
            byte[] dig = md.digest();
            int toCopy = Math.min(dig.length, keyIv.length-pos);
            System.arraycopy(dig,0,keyIv,pos,toCopy);
            pos += toCopy;
            prev = dig;
        }
        return keyIv;
    }

    public static boolean isCryptoJsEncrypted(String base64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            return new String(decoded, 0, 8, "UTF-8").equals("Salted__");
        } catch (Exception e) {
            return false;
        }
    }

    public static Component tab(MontoyaApi api){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

// ===== SECTION 1: AES PASSPHRASE =====
        JLabel aesLabel = new JLabel("Enter your AES Passphrase:");
        aesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(aesLabel);

        JTextField inputField = new JTextField();
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(inputField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

// ===== SECTION 2: CONFIGURATION =====
        JLabel configTitle = new JLabel("Configuration Settings");
        configTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        configTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(configTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

// Config Rows - Fixed to eliminate gaps
        JPanel row1 = new JPanel();
        JCheckBox checkBox1 = new JCheckBox();
        row1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Fix: Set maximum height to prevent vertical expansion
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, row1.getPreferredSize().height));

        row1.add(new JLabel("URL Parameter:"));
        row1.add(checkBox1);
        row1.add(new JLabel("Enter url parameter name:"));

        JTextField textField1 = new JTextField(10);
        row1.add(textField1);
        panel.add(row1);
        // Add small gap between config rows
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel row2 = new JPanel();
        JCheckBox checkBox2 = new JCheckBox();
        row2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Fix: Set maximum height to prevent vertical expansion
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, row2.getPreferredSize().height));

        row2.add(new JLabel("Body Parameter:"));
        row2.add(checkBox2);
        row2.add(new JLabel("Enter body parameter name:"));

        JTextField textField2 = new JTextField(10);
        row2.add(textField2);
        panel.add(row2);
        // Add small gap between config rows
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel row3 = new JPanel();
        JCheckBox checkBox3 = new JCheckBox();
        row3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row3.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Fix: Set maximum height to prevent vertical expansion
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, row3.getPreferredSize().height));

        row3.add(new JLabel("Header:"));
        row3.add(checkBox3);
        row3.add(new JLabel("Enter header name:"));

        JTextField textField3 = new JTextField(10);
        row3.add(textField3);
        panel.add(row3);

        // Add small spacing before save button
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

// ===== SECOND SAVE BUTTON =====
        JButton saveConfigButton = new JButton("Save Configuration");
        saveConfigButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveConfigButton.addActionListener(e -> {
            // Add your configuration save logic here
            aes_passphrase = inputField.getText();
            checkbox1_selected = checkBox1.isSelected();
            if (checkbox1_selected){
                url_parameter = textField1.getText();
            }
            checkbox2_selected = checkBox2.isSelected();
            if (checkbox2_selected){
                body_parameter = textField2.getText();
            }
            checkbox3_selected = checkBox3.isSelected();
            if (checkbox3_selected){
                header = textField3.getText();
            }
            JOptionPane.showMessageDialog(api.userInterface().swingUtils().suiteFrame(), "Configuration Saved");
        });
        panel.add(saveConfigButton);

        // Add glue to push everything to the top
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private static JPanel createConfigRow(String label1, String label2) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(new JLabel(label1));
        row.add(new JCheckBox());
        row.add(new JLabel(label2));

        JTextField textField = new JTextField(10);
        textField.setMaximumSize(new Dimension(200, 15));
        row.add(textField);

        return row;
    }
}
