package hust.idc.util.heap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;

public final class Queues {
	private Queues() {

	}

	public static <E> Queue<E> synchronizedQueue(Queue<E> q) {
		return new SynchronizedQueue<E>(q);
	}

	static <E> Queue<E> synchronizedQueue(Queue<E> q, Object mutex) {
		return new SynchronizedQueue<E>(q, mutex);
	}

	static class SynchronizedQueue<E> implements Queue<E>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6807342658213417900L;
		final Queue<E> queue;
		final Object mutex;

		SynchronizedQueue(Queue<E> q) {
			this.mutex = this.queue = Objects.requireNonNull(q);
		}

		SynchronizedQueue(Queue<E> q, Object mutex) {
			this.queue = Objects.requireNonNull(q);
			this.mutex = Objects.requireNonNull(mutex);
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

		@Override
		public int size() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.size();
			}
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.isEmpty();
			}
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.contains(o);
			}
		}

		@Override
		public Iterator<E> iterator() {
			// TODO Auto-generated method stub
			return queue.iterator();
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.toArray();
			}
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.toArray(a);
			}
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.remove(o);
			}
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.containsAll(c);
			}
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.addAll(c);
			}
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.removeAll(c);
			}
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				return queue.retainAll(c);
			}
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			synchronized (mutex) {
				queue.clear();
			}
		}

		@Override
		public boolean add(E e) {
			// TODO Auto-generated method stub
	        if (offer(e))
	            return true;
	        else
	            throw new IllegalStateException("Queue full");
		}
		
		public boolean equals(Object o) {synchronized(mutex){return o == this || queue.equals(o);}}
        public int hashCode()           {synchronized(mutex){return queue.hashCode();}}
        public String toString()        {synchronized(mutex){return queue.toString();}}
	}

}
