/*^
  ===========================================================================
  ACME server
  ===========================================================================
  Copyright (C) 2017-2018 DENIC eG, 60329 Frankfurt am Main, Germany
  ===========================================================================
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
  ===========================================================================
*/

package de.trustable.ca3s.acmeproxy.service.dto.problem;

import javax.annotation.concurrent.NotThreadSafe;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@NotThreadSafe
public final class InvalidAcmeMessageException extends AcmeMessageException {

  private static final long serialVersionUID = 2563474226688634942L;

  private final String title;

  public InvalidAcmeMessageException(final String message, final String acmeMessage, final Exception cause) {
    super(message + (isEmpty(acmeMessage) ? "" : " Message: '" + acmeMessage + "'"), cause);
    this.title = message;
  }

  public InvalidAcmeMessageException(final String title, final Exception cause) {
    this(title, null, cause);
  }

  public InvalidAcmeMessageException(final String message, final String acmeMessage) {
    this(message, acmeMessage, null);
  }

  public InvalidAcmeMessageException(final String message) {
    this(message, null, null);
  }

  public final String getTitle() {
    return title;
  }

}
