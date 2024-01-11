/**********************************************************************
Copyright (c) 2014 HubSpot Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **********************************************************************/
package com.hubspot.jinjava.interpret;

public class InterpretException extends RuntimeException {

  private static final long serialVersionUID = -3471306977643126138L;

  private int lineNumber = -1;
  private int startPosition = -1;

  public InterpretException(String msg) {
    super(msg);
  }

  public InterpretException(String msg, Throwable e) {
    super(msg, e);
  }

  public InterpretException(String msg, int lineNumber) {
    this(msg, lineNumber, -1);
  }

  public InterpretException(String msg, int lineNumber, int startPosition) {
    this(msg);
    this.lineNumber = lineNumber;
    this.startPosition = startPosition;
  }

  public InterpretException(String msg, Throwable e, int lineNumber, int startPosition) {
    this(msg, e);
    this.lineNumber = lineNumber;
    this.startPosition = startPosition;
  }

  public InterpretException(String msg, Throwable throwable, int lineNumber) {
    this(msg, throwable, lineNumber, -1);
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public int getStartPosition() {
    return startPosition;
  }
}
