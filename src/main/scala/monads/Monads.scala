package monads

object Monads:

  trait Monad[M[_]]:
    def unit[A](a: A): M[A]
    extension [A](m: M[A])
      def flatMap[B](f: A => M[B]): M[B]
      def map[B](f: A => B): M[B] = m.flatMap(a => unit(f(a)))

  object Monad:

    def map2[M[_]: Monad, A, B, C](m: M[A], m2: => M[B])(f: (A, B) => C): M[C] =
      m.flatMap(a => m2.map(b => f(a, b)))

    def seq[M[_]: Monad, A, B](m: M[A], m2: => M[B]): M[B] =
      map2(m, m2)((a, b) => b)
