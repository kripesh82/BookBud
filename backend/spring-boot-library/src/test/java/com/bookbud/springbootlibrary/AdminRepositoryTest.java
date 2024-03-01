package com.bookbud.springbootlibrary;

import com.bookbud.springbootlibrary.entity.Book;
import com.bookbud.springbootlibrary.repository.BookRepository;
import com.bookbud.springbootlibrary.repository.CheckoutRepository;
import com.bookbud.springbootlibrary.repository.ReviewRepository;
import com.bookbud.springbootlibrary.requestmodels.AddBookRequest;
import com.bookbud.springbootlibrary.service.AdminService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminRepositoryTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void increaseBookQuantityTest() throws Exception {
        // Prepare test data
        long bookId = 1L;
        Book book = new Book();
        book.setCopiesAvailable(5);
        book.setCopies(5);
        Optional<Book> optionalBook = Optional.of(book);

        // Mocking repository methods
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);
        when(bookRepository.save(any())).thenReturn(book);

        // Execute the method to be tested
        adminService.increaseBookQuantity(bookId);

        // Verify that the book's quantity is increased
        Assertions.assertThat(book.getCopiesAvailable()).isEqualTo(6);
        Assertions.assertThat(book.getCopies()).isEqualTo(6);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    public void decreaseBookQuantityTest() throws Exception {
        // Prepare test data
        long bookId = 1L;
        Book book = new Book();
        book.setCopiesAvailable(5);
        book.setCopies(5);
        Optional<Book> optionalBook = Optional.of(book);

        // Mocking repository methods
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);
        when(bookRepository.save(any())).thenReturn(book);

        // Execute the method to be tested
        adminService.decreaseBookQuantity(bookId);

        // Verify that the book's quantity is decreased
        Assertions.assertThat(book.getCopiesAvailable()).isEqualTo(4);
        Assertions.assertThat(book.getCopies()).isEqualTo(4);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    public void postBookTest() {
        // Prepare test data
        AddBookRequest addBookRequest = new AddBookRequest();
        addBookRequest.setTitle("Test Book");
        addBookRequest.setAuthor("Test Author");
        addBookRequest.setDescription("Test Description");
        addBookRequest.setCopies(5);
        addBookRequest.setCategory("Test Category");

        // Execute the method to be tested
        adminService.postBook(addBookRequest);

        // Verify that the book is saved successfully
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    public void deleteBookTest() throws Exception {
        // Prepare test data
        long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        Optional<Book> optionalBook = Optional.of(book);

        // Mocking repository methods
        when(bookRepository.findById(bookId)).thenReturn(optionalBook);

        // Execute the method to be tested
        adminService.deleteBook(bookId);

        // Verify that the book is deleted successfully
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).delete(book);
        verify(checkoutRepository, times(1)).deleteAllByBookId(bookId);
        verify(reviewRepository, times(1)).deleteAllByBookId(bookId);
    }
}
