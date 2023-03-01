/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.dalekcraft.structureedit.ui;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Modified from <a href="https://docs.oracle.com/javase/8/javafx/graphics-tutorial/sampleapp3d.htm">JavaFX 3D Molecule Sample Application</a>
 */
public class TransformGroup extends Group {

    public Translate translate = new Translate();
    public Translate pivot = new Translate();
    public Translate inversePivot = new Translate();
    public Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    public Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    public Rotate rotateZ = new Rotate(0, Rotate.Y_AXIS);
    public Scale scale = new Scale();

    public TransformGroup() {
        this(RotateOrder.XYZ);
    }

    public TransformGroup(RotateOrder rotateOrder) {
        getTransforms().addAll(translate, pivot);
        // choose the order of rotations based on the rotateOrder
        switch (rotateOrder) {
            case XYZ -> getTransforms().addAll(rotateZ, rotateY, rotateX);
            case XZY -> getTransforms().addAll(rotateY, rotateZ, rotateX);
            case YXZ -> getTransforms().addAll(rotateZ, rotateX, rotateY);
            case YZX -> getTransforms().addAll(rotateX, rotateZ, rotateY);  // For Camera
            case ZXY -> getTransforms().addAll(rotateY, rotateX, rotateZ);
            case ZYX -> getTransforms().addAll(rotateX, rotateY, rotateZ);
        }
        getTransforms().addAll(scale, inversePivot);
    }

    public void setRotate(double x, double y, double z) {
        rotateX.setAngle(x);
        rotateY.setAngle(y);
        rotateZ.setAngle(z);
    }

    public void setRotateX(double x) {
        rotateX.setAngle(x);
    }

    public void setRotateY(double y) {
        rotateY.setAngle(y);
    }

    public void setRotateZ(double z) {
        rotateZ.setAngle(z);
    }

    public void setScale(double scaleFactor) {
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);
        scale.setZ(scaleFactor);
    }

    // Cannot override these methods as they are final:
    // public void setScaleX(double x) { s.setX(x); }
    // public void setScaleY(double y) { s.setY(y); }
    // public void setScaleZ(double z) { s.setZ(z); }
    // Use these methods instead:
    public void setSx(double x) {
        scale.setX(x);
    }

    public void setSy(double y) {
        scale.setY(y);
    }

    public void setSz(double z) {
        scale.setZ(z);
    }

    public void setPivot(double x, double y, double z) {
        pivot.setX(x);
        pivot.setY(y);
        pivot.setZ(z);
        inversePivot.setX(-x);
        inversePivot.setY(-y);
        inversePivot.setZ(-z);
    }

    public void reset() {
        translate.setX(0.0);
        translate.setY(0.0);
        translate.setZ(0.0);
        rotateX.setAngle(0.0);
        rotateY.setAngle(0.0);
        rotateZ.setAngle(0.0);
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        pivot.setX(0.0);
        pivot.setY(0.0);
        pivot.setZ(0.0);
        inversePivot.setX(0.0);
        inversePivot.setY(0.0);
        inversePivot.setZ(0.0);
    }

    public void resetTSP() {
        translate.setX(0.0);
        translate.setY(0.0);
        translate.setZ(0.0);
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        pivot.setX(0.0);
        pivot.setY(0.0);
        pivot.setZ(0.0);
        inversePivot.setX(0.0);
        inversePivot.setY(0.0);
        inversePivot.setZ(0.0);
    }

    public enum RotateOrder {
        XYZ,
        XZY,
        YXZ,
        YZX,
        ZXY,
        ZYX
    }
}