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

import java.io.IOException;
import javax.swing.JTextArea;

public class QueryReceiver extends Thread {

        public QueryReceiver(JavaClient jc, JTextArea serverField) {
                this.jc = jc;
                this.serverField = serverField;
        }

        @Override
        public void run() {

                while(true) { //I know there is a better way, look into later
                        try {
                                String s = jc.readLine();
                                serverField.setText("Server: "+s);
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }

        }

        private JavaClient jc;
        private JTextArea serverField;
}
