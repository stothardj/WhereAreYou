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

import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.*;

public class WebApp extends BaseApplet {
        private static final long serialVersionUID = 1L;

        @Override
        public void createGUI() {
                JPanel buttonPanel = new JPanel();
                fieldPanel = new JPanel();

                bh = new ButtonHandler(fieldPanel);

                getContentPane().setLayout(new GridLayout(3, 1));

                buttonPanel.setLayout(new GridLayout(4,4));

                LinkedList<String> commands = new LinkedList<String>();
                commands.add("Create User");
                commands.add("Remove User");
                commands.add("Add Zone");
                commands.add("Remove Zone");
                commands.add("Add Friend");
                commands.add("Accept Friend");
                commands.add("Remove Friend");
                commands.add("Update Coordinate");
                commands.add("Empty All");
                commands.add("Empty Friends");
                commands.add("Empty Users");
                commands.add("Empty Zones");
                commands.add("Login");
                commands.add("Show Users");
                commands.add("Show Friends");
                commands.add("Show Zones");

                for(String li : commands) {
                        JButton b = new JButton(li);
                        b.addActionListener(bh);
                        buttonPanel.add(b);
                }

                getContentPane().add(buttonPanel);

                fieldPanel.add(new JLabel("The fields will appear here."));
                getContentPane().add(fieldPanel);

                queryPanel = new JPanel();
                queryPanel.setLayout(new GridLayout(3,1));
                queryPanel.add(new JTextField("localhost"));
                queryPanel.add(new JTextField("79"));
                JButton accept_ipport = new JButton("Accept");
                accept_ipport.addActionListener(new ActionConnect(this));
                queryPanel.add(accept_ipport);
                getContentPane().add(queryPanel);

        }

        ButtonHandler bh;
        JPanel fieldPanel, queryPanel;
}
