package studio.snowfox.albionsquare.batch.listener;

import lombok.extern.java.Log;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Log
public class GenericChunkListener implements ChunkListener {
    @Override
    public void afterChunk(ChunkContext context) {
        ChunkListener.super.afterChunk(context);

        log.info(String.format(
                "Batch processed %s items",
                context.getStepContext().getStepExecution().getReadCount()));
    }
}
