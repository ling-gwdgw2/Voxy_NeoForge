package me.cortex.voxy.client.iris;

import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.client.core.IrisVoxyRenderPipeline;
import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.minecraft.client.Minecraft;

public class VoxySamplers {
    public static void addSamplers(IrisRenderingPipeline pipeline, SamplerHolder samplers) {
        String[] opaqueNames = new String[]{"vxDepthTexOpaque", "dhDepthTex1"};
        String[] translucentNames = new String[]{"vxDepthTexTrans", "dhDepthTex", "dhDepthTex0"};

        samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> {
            if (!IrisShaderPatch.isDistantShaderShadowsEnabled()) {
                return 0;
            }
            var pipeData = ((IGetIrisVoxyPipelineData)pipeline).voxy$getPipelineData();
            if (pipeData != null && pipeData.thePipeline != null) {
                var dt = pipeData.thePipeline.fb.getDepthTex();
                if (dt != null) {
                    return dt.id;
                }
            }
            if (Minecraft.getInstance().levelRenderer instanceof IGetVoxyRenderSystem getVrs) {
                var vrs = getVrs.getVoxyRenderSystem();
                if (vrs != null && vrs.getPipeline() != null && vrs.getPipeline().fb != null) {
                    var dt = vrs.getPipeline().fb.getDepthTex();
                    if (dt != null) {
                        return dt.id;
                    }
                }
            }
            return 0;
        }, GlSampler.NEAREST, opaqueNames);

        samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> {
            if (!IrisShaderPatch.isDistantShaderShadowsEnabled()) {
                return 0;
            }
            var pipeData = ((IGetIrisVoxyPipelineData)pipeline).voxy$getPipelineData();
            if (pipeData != null && pipeData.thePipeline != null) {
                var dt = pipeData.thePipeline.fbTranslucent.getDepthTex();
                if (dt != null) {
                    return dt.id;
                }
            }
            if (Minecraft.getInstance().levelRenderer instanceof IGetVoxyRenderSystem getVrs) {
                var vrs = getVrs.getVoxyRenderSystem();
                if (vrs != null && vrs.getPipeline() instanceof IrisVoxyRenderPipeline irp && irp.fbTranslucent != null) {
                    var dt = irp.fbTranslucent.getDepthTex();
                    if (dt != null) {
                        return dt.id;
                    }
                }
                if (vrs != null && vrs.getPipeline() != null && vrs.getPipeline().fb != null) {
                    var dt = vrs.getPipeline().fb.getDepthTex();
                    if (dt != null) {
                        return dt.id;
                    }
                }
            }
            return 0;
        }, GlSampler.NEAREST, translucentNames);
    }
}
