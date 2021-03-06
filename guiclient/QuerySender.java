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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import java.awt.*;

import javax.swing.*;


public class QuerySender implements ActionListener {

        public QuerySender(JavaClient jc, JPanel fieldPanel, JPanel queryPanel, ButtonHandler bh) throws UnknownHostException, IOException {
                this.fieldPanel = fieldPanel;
                this.queryPanel = queryPanel;
                this.bh = bh;
                this.jc = jc;
        }
        public void actionPerformed(ActionEvent ae) {

                //Generate JSON
                String running = "{ \"Request Type\" : \"" + bh.getRequestType() + "\" , ";
                for(int i=0; i < fieldPanel.getComponentCount() - 1; i += 2) {
                        JLabel tl = (JLabel)fieldPanel.getComponent(i);
                        String tls = tl.getText();
                        String tfs = "";
                        //Depending on what the text label is, process it accordingly.
                        if (!(tls.equals("Account Type") || tls.equals("Action"))) {
                                JTextField tf = (JTextField)fieldPanel.getComponent(i + 1);
                                tfs = tf.getText();
                        }
                        else {
                                JPanel radioPanel = (JPanel)fieldPanel.getComponent(i + 1);
                                Component[] components = radioPanel.getComponents();
                                for (int j=0; j < components.length; j++) {
                                        JRadioButton rb = (JRadioButton)components[j];
                                        if (rb.isSelected()) {
                                                tfs = rb.getText();
                                                break;
                                        }
                                }
                        }

                        if(tls.equals("Lat") || tls.equals("Lon") || tls.equals("Radius")) //float
                                running += "\""+tls+"\" : "+ tfs + ", ";
                        else //string
                                running += "\""+tls+"\" : \""+ tfs + "\", ";
                }
                running = running.substring(0, running.length() - 2);
                running += "}";
                //System.out.println(running);
                ((JTextArea)queryPanel.getComponent(0)).setText("Client: "+running);

                //Send query to server
                jc.sendLine(running);
        }

        private ButtonHandler bh;
        private JavaClient jc;
        private JPanel fieldPanel, queryPanel;
}
