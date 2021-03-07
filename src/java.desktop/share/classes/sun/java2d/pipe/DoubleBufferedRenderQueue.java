package sun.java2d.pipe;

import java.util.Collection;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class DoubleBufferedRenderQueue implements RenderQueue {
  private volatile RenderQueue nativeRenderQueue;
  private volatile RenderQueue javaRenderQueue;

  public DoubleBufferedRenderQueue() {
    nativeRenderQueue = createRenderQueueImpl();
    javaRenderQueue = createRenderQueueImpl();
  }

  public abstract RenderQueue createRenderQueueImpl();

  @Override
  public Collection<Object> copyAndClearReferences() {
    return javaRenderQueue.copyAndClearReferences();
  }

  @Override
  public void addReference(Object ref) {
    javaRenderQueue.addReference(ref);
  }

  @Override
  public RenderBuffer getBuffer() {
    return javaRenderQueue.getBuffer();
  }

  @Override
  public void ensureCapacity(int opsize) {
    javaRenderQueue.ensureCapacity(opsize);
  }

  @Override
  public void ensureCapacityAndAlignment(int opsize, int first8ByteValueOffset) {
    javaRenderQueue.ensureCapacityAndAlignment(opsize, first8ByteValueOffset);
  }

  @Override
  public void ensureAlignment(int first8ByteValueOffset) {
    javaRenderQueue.ensureAlignment(first8ByteValueOffset);
  }

  public void flushNow(int position, boolean sync) {
    javaRenderQueue.getBuffer().position(position);
    flushNow(sync);
  }

  public void togglePipelines() {
    final RenderQueue tmp = nativeRenderQueue;
    nativeRenderQueue = javaRenderQueue;
    javaRenderQueue = tmp;
  }


    // system property utilities
    public static int getInteger(final String key, final int def,
                                 final int min, final int max)
    {
        final String property = AccessController.doPrivileged(
                                    new GetPropertyAction(key));

        int value = def;
        if (property != null) {
            try {
                value = Integer.decode(property);
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer value for " + key + " = " + property);
            }
        }

        // check for invalid values
        if ((value < min) || (value > max)) {
            System.out.println("Invalid value for " + key + " = " + value
                    + "; expected value in range[" + min + ", " + max + "] !");
            value = def;
        }
        return value;
    }

    protected static int align(final int val, final int norm) {
        final int ceil = (int)Math.ceil( ((float) val) / norm);
        return ceil * norm;
    }
}
