package com.marklogic.jedit;

import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.Buffer;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 4, 2004
 * Time: 12:54:02 PM
 */
public interface BufferUpdateListener
{
	void bufferUpdated (BufferUpdate bufferUpdate);
	void bufferRenamed (Buffer buffer, String oldPath, String newPath);
}
