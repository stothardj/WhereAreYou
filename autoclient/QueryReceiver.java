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

public class QueryReceiver extends Thread {

        public QueryReceiver(JavaClient jc, String user) {
                this.jc = jc;
                this.user = user;
        }

        @Override
        public void run() {
                while(jc.isConnected()) { //I know there is a better way, look into later
                        try {
                                String s = jc.readLine();
                                if(s == null)
                                        jc.disconnect();
                                else
                                        System.out.println("Server to "+user+": "+s);
                        } catch (IOException e) {
                                //Note: This may seem ugly, but we want to be able to disconnect at any point
                                //System.err.println("This stack trace is supposed to happen ------");
                                //e.printStackTrace();
                                //System.err.println("The previous stack trace was supposed to happen ------");
                                try {
                                        jc.disconnect();
                                } catch (IOException e1) {
                                        e1.printStackTrace();
                                }
                        }
                }

        }

        private JavaClient jc;
        private String user;
}
