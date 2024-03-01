package com.bookbud.springbootlibrary;

import com.bookbud.springbootlibrary.entity.Book;
import com.bookbud.springbootlibrary.entity.Checkout;
import com.bookbud.springbootlibrary.repository.BookRepository;
import com.bookbud.springbootlibrary.repository.CheckoutRepository;
import com.bookbud.springbootlibrary.repository.HistoryRepository;
import com.bookbud.springbootlibrary.service.BookService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookRepositoryTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void checkoutBook_Success() throws Exception {
        // Prepare test data
        String userEmail = "test@example.com";
        long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setCopiesAvailable(1);
        Optional<Book> optionalBook = Optional.of(book);
        Checkout existingCheckout = null;

        // Mocking repository methods
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(existingCheckout);

        // Execute the method to be tested
        Book checkedOutBook = bookService.checkoutBook(userEmail, bookId);

        // Verify that the book is checked out successfully
        assertNotNull(checkedOutBook);
        assertEquals(0, checkedOutBook.getCopiesAvailable());
        verify(bookRepository, times(1)).findById(bookId);
        verify(checkoutRepository, times(1)).findByUserEmailAndBookId(userEmail, bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(checkoutRepository, times(1)).save(any(Checkout.class));
    }



    @Test
    @Order(2)
    @Rollback(value = false)
    public void checkoutBook_BookAlreadyCheckedOut() {
        // Prepare test data
        String userEmail = "test@example.com";
        long bookId = 1L;
        Book book = new Book();
        book.setCopiesAvailable(0);
        Optional<Book> optionalBook = Optional.of(book);
        Checkout existingCheckout = new Checkout();

        // Mocking repository methods
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(existingCheckout);

        // Execute the method to be tested and assert the exception
        Exception exception = assertThrows(Exception.class, () -> bookService.checkoutBook(userEmail, bookId));

        // Verify that the correct exception is thrown
        assertEquals("Book doesn't exist or already checked out by user", exception.getMessage());

        // Verify interactions with repositories
        verify(bookRepository, times(1)).findById(bookId);
        verify(checkoutRepository, times(1)).findByUserEmailAndBookId(userEmail, bookId);
        verify(bookRepository, never()).save(any());
        verify(checkoutRepository, never()).save(any());
    }





    @Test
    @Order(3)
    @Rollback(value = false)
    public void returnBook_BookNotCheckedOut() {
        // Prepare test data
        String userEmail = "test@example.com";
        long bookId = 1L;
        Book book = new Book();
        Optional<Book> optionalBook = Optional.of(book);
        Checkout checkout = null;

        // Mocking repository methods
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(checkout);

        // Execute the method to be tested and assert the exception
        try {
            bookService.returnBook(userEmail, bookId);
        } catch (Exception exception) {
            // Verify that the correct exception is thrown
            assertEquals("Book does not exist or not checked out by user", exception.getMessage());
        }

        // Verify interactions with repositories
        verify(bookRepository, times(1)).findById(bookId);
        verify(checkoutRepository, times(1)).findByUserEmailAndBookId(userEmail, bookId);
        verify(bookRepository, never()).save(any());
        verify(checkoutRepository, never()).deleteById(any(Long.class));
        verify(historyRepository, never()).save(any());
    }


    @Test
    @Order(4)
    @Rollback(value = false)
    public void currentLoansCount_Test() {
        // Prepare test data
        String userEmail = "test@example.com";
        List<Checkout> checkouts = new ArrayList<>();
        checkouts.add(new Checkout());
        checkouts.add(new Checkout());

        // Mocking repository methods
        when(checkoutRepository.findBooksByUserEmail(userEmail)).thenReturn(checkouts);

        // Execute the method to be tested
        int count = bookService.currentLoansCount(userEmail);

        // Verify the count of current loans
        assertEquals(2, count);
        verify(checkoutRepository, times(1)).findBooksByUserEmail(userEmail);
    }





    @Test
    @Order(5)
    @Rollback(value = false)
    public void renewLoan_BookNotCheckedOut() {
        // Prepare test data
        String userEmail = "test@example.com";
        long bookId = 1L;
        Checkout checkout = null;

        // Mocking repository methods
        when(checkoutRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(checkout);

        // Execute the method to be tested and assert the exception
        Exception exception = assertThrows(Exception.class, () -> bookService.renewLoan(userEmail, bookId));

        // Verify that the correct exception is thrown
        assertEquals("Book does not exist or not checked out by user", exception.getMessage());
        verify(checkoutRepository, times(1)).findByUserEmailAndBookId(userEmail, bookId);
        verify(checkoutRepository, never()).save(any(Checkout.class));
    }
}
