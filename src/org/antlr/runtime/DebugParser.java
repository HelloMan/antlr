/*
 [The "BSD licence"]
 Copyright (c) 2004 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.runtime;

public class DebugParser extends Parser {

	/** The default debugger mimics the traceParser behavior of ANTLR 2.x */
	class TraceDebugger implements ANTLRDebugInterface {
		protected int level = 0;
		public void enterRule(String ruleName) {
			for (int i=1; i<=level; i++) {System.out.print(" ");}
			CharStream cs = input.getTokenSource().getCharStream();
			System.out.println("> "+ruleName+" LT(1)="+input.LT(1).toString(cs));
			level++;
		}
		public void exitRule(String ruleName) {
			level--;
			for (int i=1; i<=level; i++) {System.out.print(" ");}
			CharStream cs = input.getTokenSource().getCharStream();
			System.out.println("< "+ruleName+" LT(1)="+input.LT(1).toString(cs));
		}
		public void enterAlt(int alt) {}
		public void enterSubRule() {}
		public void exitSubRule() {}
		public void location(int line, int pos) {}
		public void consumeToken(Token token) {}
		public void LT(int i) {}
		public void recognitionException(RecognitionException e) {}
		public void recovered(Token t) {}
	}

	/** Who to notify when events in the parser occur. */
	protected ANTLRDebugInterface dbg = null;

	/** Create a normal parser except wrap the token stream in a debug
	 *  proxy that fires consume events.
	 */
	public DebugParser(TokenStream input, ANTLRDebugInterface dbg) {
		super(new DebugTokenStream(input,dbg));
		setDebugListener(dbg);
	}

	public DebugParser(TokenStream input) {
		super(input);
		setDebugListener(new TraceDebugger());
	}

	/** Provide a new debug event listener for this parser.  Notify the
	 *  input stream too that it should send events to this listener.
	 */
	public void setDebugListener(ANTLRDebugInterface dbg) {
		if ( input instanceof DebugTokenStream ) {
			((DebugTokenStream)input).setDebugListener(dbg);
		}
		this.dbg = dbg;
	}

	public void match(int ttype, BitSet follow) throws MismatchedTokenException {
		boolean before = this.errorRecovery;
		Token t = input.LT(1);
		super.match(ttype, follow);
		boolean after = this.errorRecovery;
		// if was in recovery and is not now, trigger recovered event
		if ( before && !after ) {
			dbg.recovered(t);
		}
	}

	public void reportError(RecognitionException e) {
		super.reportError(e);
		dbg.recognitionException(e);
	}

}
