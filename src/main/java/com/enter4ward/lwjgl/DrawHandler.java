package com.enter4ward.lwjgl;

import com.enter4ward.math.Group;
import com.enter4ward.math.IObject3D;
import org.joml.Matrix4f;

public interface DrawHandler {

    Matrix4f onDraw(IObject3D obj, Group group, BufferObject buffer);


}
