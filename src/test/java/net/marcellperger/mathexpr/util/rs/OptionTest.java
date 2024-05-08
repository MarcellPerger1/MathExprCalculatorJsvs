package net.marcellperger.mathexpr.util.rs;

import net.marcellperger.mathexpr.MiniMock.*;
import net.marcellperger.mathexpr.util.rs.Option.Some;
import net.marcellperger.mathexpr.util.rs.Option.None;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {
    Some<Integer> getSome() {
        return Option.newSome(314);
    }
    None<Integer> getNone() {
        return Option.newNone();
    }

    @Test
    void isSome() {
        assertTrue(getSome().isSome());
        assertFalse(getNone().isSome());
    }

    @Test
    void isNone() {
        assertFalse(getSome().isNone());
        assertTrue(getNone().isNone());
    }

    @Test
    void isSomeAnd() {
        MockedPredicate<Integer> mfFalse = new MockedPredicate<>(false);
        MockedPredicate<Integer> mfTrue = new MockedPredicate<>(true);
        {
            assertTrue(getSome().isSomeAnd(mfTrue));
            mfTrue.assertCalledOnceWith(314);
            assertFalse(getSome().isSomeAnd(mfFalse));
            mfFalse.assertCalledOnceWith(314);
        }
        mfTrue.reset();
        mfFalse.reset();
        {
            assertFalse(getNone().isSomeAnd(mfTrue));
            mfTrue.assertNotCalled();
            assertFalse(getNone().isSomeAnd(mfFalse));
            mfFalse.assertNotCalled();
        }
    }

    @Test
    void map() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(Option.newSome(7), Option.newSome(6).map(mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(Option.newNone(), Option.<Integer>newNone().map(mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void mapOr() {
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        assertEquals(7, Option.newSome(6).mapOr(-1, mfAdd1));
        mfAdd1.assertCalledOnceWith(6);
        mfAdd1.reset();
        assertEquals(-1, Option.<Integer>newNone().mapOr(-1, mfAdd1));
        mfAdd1.assertNotCalled();
    }

    @Test
    void mapOrElse() {
        MockedSupplier<Integer> mSupplier = new MockedSupplier<>(-6);
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        {
            assertEquals(7, Option.newSome(6).mapOrElse(mSupplier, mfAdd1));
            mfAdd1.assertCalledOnceWith(6);
            mSupplier.assertNotCalled();
        }
        mfAdd1.reset();
        mSupplier.reset();
        {
            assertEquals(-6, Option.<Integer>newNone().mapOrElse(mSupplier, mfAdd1));
            mfAdd1.assertNotCalled();
            mSupplier.assertCalledOnce();
        }
    }

    @Test
    void ifThenElse__func_supp() {
        MockedSupplier<Integer> mSupplier = new MockedSupplier<>(-6);
        MockedFunction<Integer, Integer> mfAdd1 = new MockedFunction<>(i -> i + 1);
        {
            assertEquals(7, Option.newSome(6).ifThenElse(mfAdd1, mSupplier));
            mfAdd1.assertCalledOnceWith(6);
            mSupplier.assertNotCalled();
        }
        mfAdd1.reset();
        mSupplier.reset();
        {
            assertEquals(-6, Option.<Integer>newNone().ifThenElse(mfAdd1, mSupplier));
            mfAdd1.assertNotCalled();
            mSupplier.assertCalledOnce();
        }
    }

    @Test
    void ifThenElse__cons_runnable() {
        MockedConsumer<Integer> mCons = new MockedConsumer<>();
        MockedRunnable mRunnable = new MockedRunnable();
        {
            getSome().ifThenElse(mCons, mRunnable);
            mCons.assertCalledOnceWith(314);
            mRunnable.assertNotCalled();
        }
        mCons.reset();
        mRunnable.reset();
        {
            getNone().ifThenElse(mCons, mRunnable);
            mCons.assertNotCalled();
            mRunnable.assertCalledOnce();
        }
    }

    @Test
    void inspect() {
        MockedConsumer<Integer> intCons = new MockedConsumer<>();
        assertEquals(getSome(), getSome().inspect(intCons));
        intCons.assertCalledOnceWith(314);
        intCons.reset();
        assertEquals(getNone(), getNone().inspect(intCons));
        intCons.assertNotCalled();
    }

    @Test
    void inspectErr() {
        MockedRunnable mRunnable = new MockedRunnable();
        assertEquals(getNone(), getNone().inspectErr(mRunnable));
        mRunnable.assertCalledOnce();
        mRunnable.reset();
        assertEquals(getSome(), getSome().inspectErr(mRunnable));
        mRunnable.assertNotCalled();
    }
}