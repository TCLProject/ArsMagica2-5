package com.tfc.minecraft_effekseer_implementation;

import com.google.common.collect.Queues;
import java.util.Deque;

import com.tfc.minecraft_effekseer_implementation.vector.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.tclproject.mysteriumlib.future.PortUtil;

@SideOnly(Side.CLIENT)
public class MatrixStack {
    private final Deque<Entry> stack = PortUtil.make(Queues.newArrayDeque(), (matrixQueue) -> {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setIdentity();
        matrixQueue.add(new Entry(matrix4f, matrix3f));
    });

    public void translate(double x, double y, double z) {
        Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.multiply(Matrix4f.createTranslateMatrix((float)x, (float)y, (float)z));
    }

    public void addTheLastThreeMatrixNumbers(double num1, double num2, double num3) {
        Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.m03 += num1;
        matrixstack$entry.matrix.m13 += num2;
        matrixstack$entry.matrix.m23 += num3;
    }

    public void scale(float x, float y, float z) {
        Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.multiply(Matrix4f.createScaleMatrix(x, y, z));
        if (x == y && y == z) {
            if (x > 0.0F) {
                return;
            }

            matrixstack$entry.normal.mul(-1.0F);
        }

        float f = 1.0F / x;
        float f1 = 1.0F / y;
        float f2 = 1.0F / z;
        float f3 = fastInvCubeRoot(f * f1 * f2);
        matrixstack$entry.normal.mul(Matrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
    }

    public static float fastInvCubeRoot(float p_226166_0_) {
        int i = Float.floatToIntBits(p_226166_0_);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
        return 0.6666667F * f + 1.0F / (3.0F * f * f * p_226166_0_);
    }

    public void rotate(Quaternion quaternion) {
        Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.multiply(quaternion);
        matrixstack$entry.normal.mul(quaternion);
    }

    public void push() {
        Entry matrixstack$entry = this.stack.getLast();
        this.stack.addLast(new Entry(matrixstack$entry.matrix.copy(), matrixstack$entry.normal.copy()));
    }

    public void pop() {
        this.stack.removeLast();
    }

    public Entry getLast() {
        return this.stack.getLast();
    }

    public boolean clear() {
        return this.stack.size() == 1;
    }

    @SideOnly(Side.CLIENT)
    public static final class Entry {
        private final Matrix4f matrix;
        private final Matrix3f normal;

        private Entry(Matrix4f matrix, Matrix3f normal) {
            this.matrix = matrix;
            this.normal = normal;
        }

        public Matrix4f getMatrix() {
            return this.matrix;
        }

        public Matrix3f getNormal() {
            return this.normal;
        }
    }
}