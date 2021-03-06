package com.superslow.locker.burst;

import com.github.shchurov.particleview.Particle;

class LPBurstParticle extends Particle {

    private static final int SIZE = 64;

    float vx;
    float vy;
    float vr;
    double timeLeft;

    LPBurstParticle() {
        super(SIZE, SIZE, 0, 0, 0);
    }

    void setup(float x, float y, int textureIndex, float vx, float vy, float vr, double timeLeft) {
        setX(x);
        setY(y);
        setTextureIndex(textureIndex);
        this.vx = vx;
        this.vy = vy;
        this.vr = vr;
        this.timeLeft = timeLeft;
    }
}
