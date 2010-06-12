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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.util.*;

public class ButtonHandler implements ActionListener {

        public ButtonHandler(JPanel fieldPanel) {
                this.fieldPanel = fieldPanel;
                this.currentRequestType = "Create User";
        }
        public void actionPerformed(ActionEvent ae) {
                JButton src = ((JButton)ae.getSource());
                HashMap<String,String[]> fields = new HashMap<String,String[]>();
                HashMap<String,String[]> multipleChoice = new HashMap<String,String[]>();

                fields.put("Create User", new String[] {"First Name", "Last Name",
                                "User Name", "Password", "Account Type"} );
                fields.put("Remove User", new String[] {});
                fields.put("Add Zone", new String[] {"Zone Name", "Lat", "Lon", "Radius", "Action", "Text"});
                fields.put("Remove Zone", new String[] {"Zone Name"});
                fields.put("Add Friend", new String[] {"Friend Name"});
                fields.put("Accept Friend", new String[] {"Friend Name"});
                fields.put("Remove Friend", new String[] {"Friend Name"});
                fields.put("Update Coordinate", new String[] {"Lat", "Lon"});
                fields.put("Empty All", new String[] {});
                fields.put("Empty Friends", new String[] {});
                fields.put("Empty Users", new String[] {});
                fields.put("Empty Zones", new String[] {});
                fields.put("Login", new String[] {"User Name", "Password"});
                fields.put("Show Users", new String[] {});
                fields.put("Show Friends", new String[] {});
                fields.put("Show Zones", new String[] {});

                multipleChoice.put("Action", new String[] {"SHOWGPS", "SHOWTEXT", "HIDE"});
                multipleChoice.put("Account Type", new String[] {"User"});

                fieldPanel.removeAll();
                currentRequestType = src.getText();
                String[] arr = fields.get(currentRequestType);
                if(arr.length > 0) {
                        fieldPanel.setLayout(new GridLayout(arr.length, 2));
                        for(String field : arr) {
                                JLabel flabel = new JLabel(field);
                                fieldPanel.add(flabel);
                                if (!(field.equals("Action") || field.equals("Account Type"))) {
                                        fieldPanel.add(new JTextField());
                                }
                                else if (field.equals("Action")) {
                                        ButtonGroup action = new ButtonGroup();
                                        String[] options = multipleChoice.get(field);
                                        JPanel radioPanel = new JPanel(new GridLayout(1, 0));
                                        for (String option : options) {
                                                JRadioButton radio = new JRadioButton(option);
                                                action.add(radio);
                                                //radio.addActionListener(this);
                                                radioPanel.add(radio);
                                        }
                                        fieldPanel.add(radioPanel, BorderLayout.LINE_START);
                                }
                                else if (field.equals("Account Type")) {
                                        ButtonGroup acctType = new ButtonGroup();
                                        JPanel radioPanel = new JPanel(new GridLayout(1,0));
                                        for (String option : multipleChoice.get(field)) {
                                                JRadioButton radio = new JRadioButton(option);
                                                acctType.add(radio);
                                                //radio.addActionListener(this);
                                                radioPanel.add(radio);
                                        }
                                        fieldPanel.add(radioPanel, BorderLayout.LINE_START);
                                }
                        }
                } else {
                        fieldPanel.setLayout(new BorderLayout());
                        fieldPanel.add(new JLabel(currentRequestType+" requires no fields. Hit send query and it will go."));
                }

                fieldPanel.revalidate();
                fieldPanel.repaint();
        }

        public String getRequestType() {
                return currentRequestType;
        }

        private String currentRequestType;
        private JPanel fieldPanel;

}
