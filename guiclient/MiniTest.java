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

import java.util.LinkedList;

public class MiniTest {
        public static void main(String args[]) {
                final LinkedList<String> l = new LinkedList<String>();
                l.add("I am not sure");
                l.add("C++ would let me");
                l.add("do this to a const.");
                for(String a : l) {
                        System.out.println(a);
                }
        }
}
