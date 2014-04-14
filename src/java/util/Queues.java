package java.util;

public class Queues {
	private Queues(){
		
	}
	
	public static <E> Queue<E> synchronizedQueue(Queue<E> q){
		return new SynchronizedQueue<E>(q);
	}
	
	static <E> Queue<E> synchronizedQueue(Queue<E> q, Object mutex){
		return new SynchronizedQueue<E>(q, mutex);
	}
	
	static class SynchronizedQueue<E> extends Collections.SynchronizedCollection<E> implements Queue<E>{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -6807342658213417900L;
		final Queue<E> queue;
		
		SynchronizedQueue(Queue<E> q){
			super(q);
			this.queue = q;
		}
		
		SynchronizedQueue(Queue<E> q, Object mutex){
			super(q, mutex);
			this.queue = q;
		}

		@Override
		public boolean offer(E e) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.offer(e);
			}
		}

		@Override
		public E poll() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.poll();
			}
		}

		@Override
		public E peek() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.peek();
			}
		}

		@Override
		public E remove() {
			E x = poll();
			if (x != null)
				return x;
			else
				throw new NoSuchElementException();
		}

		@Override
		public E element() {
			E x = peek();
			if (x != null)
				return x;
			else
				throw new NoSuchElementException();
		}
	}

}
