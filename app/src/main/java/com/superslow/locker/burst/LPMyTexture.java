package com.superslow.locker.burst;


import android.content.res.Resources;

import com.superslow.locker.R;
import com.github.shchurov.particleview.SimpleTextureAtlasPacker;
import com.github.shchurov.particleview.TextureAtlas;

import java.util.Arrays;
import java.util.List;

public class LPMyTexture implements com.github.shchurov.particleview.TextureAtlasFactory {

    public static final int TEXTURE_COUNT = 16;

    private Resources resources;

    public LPMyTexture(Resources resources) {
        this.resources = resources;
    }

    @Override
    public TextureAtlas createTextureAtlas() {
        List<Integer> drawables = Arrays.asList(R.drawable.texture0, R.drawable.texture1, R.drawable.texture2, R.drawable.texture3,
                R.drawable.texture4, R.drawable.texture5, R.drawable.texture6, R.drawable.texture7, R.drawable.texture8, R.drawable.texture9,
                R.drawable.texture10, R.drawable.texture11, R.drawable.texture12, R.drawable.texture13, R.drawable.texture14,
                R.drawable.texture15);
        SimpleTextureAtlasPacker packer = new SimpleTextureAtlasPacker();
        return packer.pack(drawables, resources, 300, 300);
    }
}
