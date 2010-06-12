/*
Copyright 2010 Jake Stothard, Brian Garfinkel, Adam Shwert, Hongchen Yu, Yijie Wang, Ryan Rosario, Jiho Kim

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ActionConnect implements ActionListener {

        public ActionConnect(WebApp wa) {
                this.wa = wa;
        }
        public void actionPerformed(ActionEvent arg0) {

                try {
                        String host = ((JTextField)wa.queryPanel.getComponent(0)).getText();
                        int port = Integer.parseInt(((JTextField)wa.queryPanel.getComponent(1)).getText());
                        wa.queryPanel.setLayout(new GridLayout(3,1));
                        JButton sendQuery = new JButton("Send Query");
                        JavaClient jc = new JavaClient(host, port);
                        jc.connect();
                        sendQuery.addActionListener(new QuerySender(jc , wa.fieldPanel, wa.queryPanel, wa.bh));
                        wa.queryPanel.removeAll();
                        JTextArea cta = new JTextArea("Client: ");
                        cta.setLineWrap(true);
                        cta.setEditable(false);
                        wa.queryPanel.add(cta);
                        JTextArea sta = new JTextArea("Server: ");
                        sta.setLineWrap(true);
                        sta.setEditable(false);
                        wa.queryPanel.add(sta);
                        wa.queryPanel.add(sendQuery);
                        QueryReceiver qr = new QueryReceiver(jc, sta);
                        qr.start();

                        wa.queryPanel.revalidate();
                        wa.queryPanel.repaint();
                } catch (Exception e) {
                        e.printStackTrace();
                        JLabel errorMessage = new JLabel("Unable to connect to server.");
                        errorMessage.setForeground(Color.red);
                        wa.queryPanel.removeAll();
                        wa.queryPanel.add(errorMessage);
                        wa.queryPanel.revalidate();
                        wa.queryPanel.repaint();
                }

        }
        private WebApp wa;
}
