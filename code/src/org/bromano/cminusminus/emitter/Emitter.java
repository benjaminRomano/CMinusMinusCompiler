package org.bromano.cminusminus.emitter;

import org.bromano.cminusminus.nodes.Program;

public interface Emitter {
    String emit(Program program) throws EmitterException;
}
