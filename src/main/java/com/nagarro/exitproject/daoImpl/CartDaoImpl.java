package com.nagarro.exitproject.daoImpl;

import java.util.List;


import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nagarro.exitproject.dao.ICartDao;
import com.nagarro.exitproject.model.Cart;
import com.nagarro.exitproject.model.CartProductEntries;
import com.nagarro.exitproject.model.Customer;
import com.nagarro.exitproject.model.Product;

@Repository
public class CartDaoImpl implements ICartDao{

	@Autowired
	private SessionFactory sessionFactory;
	Logger logger = Logger.getLogger(CartDaoImpl.class);

	public List<CartProductEntries> getCart(int cid) {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query custQuery = session.createQuery("from Customer where id=:id")
					.setParameter("id", cid);
			Customer customer = (Customer) custQuery.uniqueResult();

			// Get the cartId
			Cart cart = (Cart) session
					.createQuery("from Cart where customer=:cust")
					.setParameter("cust", customer).uniqueResult();

			@SuppressWarnings("unchecked")
			List<CartProductEntries> cpentries = session
					.createQuery("from CartProductEntries where cart=:cart")
					.setParameter("cart", cart).list();
			return cpentries;

		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	public boolean decreaseQuantity(int pid, int cid) {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query custQuery = session.createQuery("from Customer where id=:id")
					.setParameter("id", cid);
			Customer customer = (Customer) custQuery.uniqueResult();

			Cart cart = (Cart) session
					.createQuery("from Cart where customer=:cust")
					.setParameter("cust", customer).uniqueResult();
			Product product = (Product) session
					.createQuery("from Product where id=:id")
					.setParameter("id", pid).uniqueResult();

			CartProductEntries cpentry = (CartProductEntries) session
					.createQuery(
							"from CartProductEntries where product=:prod and cart=:cart")
					.setParameter("prod", product).setParameter("cart", cart)
					.uniqueResult();

			int quantity = cpentry.getQuantity();
			if (!(quantity - 1 < 0)) {
				cpentry.setQuantity(cpentry.getQuantity() - 1);
				return true;
			} else
				return false;
		} catch (Exception e) {
            logger.error(e);
			return false;
		}
	}

	public boolean increaseQuantity(int pid, int cid) {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query custQuery = session.createQuery("from Customer where id=:id")
					.setParameter("id", cid);
			Customer customer = (Customer) custQuery.uniqueResult();

			Cart cart = (Cart) session
					.createQuery("from Cart where customer=:cust")
					.setParameter("cust", customer).uniqueResult();
			Product product = (Product) session
					.createQuery("from Product where id=:id")
					.setParameter("id", pid).uniqueResult();

			CartProductEntries cpentry = (CartProductEntries) session
					.createQuery(
							"from CartProductEntries where product=:prod and cart=:cart")
					.setParameter("prod", product).setParameter("cart", cart)
					.uniqueResult();

			int quantity = cpentry.getQuantity();
			int stock = product.getStock();
			if (quantity + 1 < stock) { // Valid
				cpentry.setQuantity(cpentry.getQuantity() + 1);
				return true;
			} else
				return false;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}

	}
    
	public boolean deleteCart(int cid) {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query custQuery = session.createQuery("from Customer where id=:id")
					.setParameter("id", cid);
			Customer customer = (Customer) custQuery.uniqueResult();
			
			Cart cart = (Cart) session
					.createQuery("from Cart where customer=:cust")
					.setParameter("cust", customer).uniqueResult();
			session.delete(cart);
			return true;

		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}
	public boolean deleteFromCart(int pid, int cid) {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query custQuery = session.createQuery("from Customer where id=:id")
					.setParameter("id", cid);
			Customer customer = (Customer) custQuery.uniqueResult();

			// Get the cartId
			Cart cart = (Cart) session
					.createQuery("from Cart where customer=:cust")
					.setParameter("cust", customer).uniqueResult();
			Product product = (Product) session
					.createQuery("from Product where id=:id")
					.setParameter("id", pid).uniqueResult();

			CartProductEntries cpentry = (CartProductEntries) session
					.createQuery(
							"from CartProductEntries where product=:prod and cart=:cart")
					.setParameter("prod", product).setParameter("cart", cart)
					.uniqueResult();

			session.delete(cpentry);
			return true;

		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	public boolean addProductToCart(int pid, int cid, int quantity) {
		Session session = this.sessionFactory.getCurrentSession();
		try {
			Query custQuery = session.createQuery("from Customer where id=:id")
					.setParameter("id", cid);
			Customer customer = (Customer) custQuery.uniqueResult();
			
			Product product = (Product) session
					.createQuery("from Product where id=:id")
					.setParameter("id", pid).uniqueResult();

			Query cartQuery = session.createQuery(
					"from Cart where customer=:cust").setParameter("cust",
					customer);
			Cart cart = null;
			cart = (Cart) cartQuery.uniqueResult();

			if (cart == null) {
				cart = new Cart();
				cart.setCustomer(customer);
				session.save(cart);
				// To get the id of the newly added cart.
				Cart addedCart = (Cart) session
						.createQuery("from Cart where customer=:cust")
						.setParameter("cust", customer).uniqueResult();
				CartProductEntries cpentry = new CartProductEntries();
				cpentry.setProduct(product);
				cpentry.setCart(addedCart);
				cpentry.setQuantity(quantity);
				session.save(cpentry);
				return true;
			}

			CartProductEntries cpentry = new CartProductEntries();
			cpentry.setProduct(product);
			cpentry.setCart(cart);
			cpentry.setQuantity(quantity);
			session.save(cpentry);			
			return true;

		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

}
