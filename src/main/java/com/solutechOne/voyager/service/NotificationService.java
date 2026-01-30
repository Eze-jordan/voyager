package com.solutechOne.voyager.service;

import com.solutechOne.voyager.model.Company;
import com.solutechOne.voyager.model.Manager;
import com.solutechOne.voyager.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void envoyerIdentifiantsUser(User user, String rawPassword) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@solutech-one.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Vos identifiants Voyager");

            String html = """
        <div style="font-family: Arial, sans-serif; background-color: #4ca0e0; padding: 30px;">
          <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
            <h2 style="text-align: center; color: #2c3e50;">Bienvenue sur Voyager</h2>
            <p>Bonjour <strong>%s %s</strong>,</p>
            <p>Votre compte user a été créé avec succès.</p>

            <p><strong>Vos informations :</strong></p>
            <ul>
              <li><strong>ID user :</strong> %s</li>
              <li><strong>Identifiant (email) :</strong> %s</li>
              <li><strong>Mot de passe (temporaire) :</strong> %s</li>
            </ul>

            <p style="color: red;">⚠️ Veuillez modifier ce mot de passe lors de votre première connexion.</p>

            <p style="color: #888; font-size: 12px; text-align: center;">
              Si vous n'êtes pas à l'origine de cette création, ignorez ce message.
            </p>
            <p style="text-align: center; color: #aaa; margin-top: 20px;">— Voyager</p>
          </div>
        </div>
        """.formatted(
                    user.getName(),
                    user.getFirstname(),
                    user.getId(),
                    user.getEmail(),
                    rawPassword
            );

            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }
    public void envoyerIdentifiantsCompany(Company company, String rawPassword) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@solutech-one.com");
            helper.setTo(company.getEmail());
            helper.setSubject("Vos identifiants Voyager");

            String html = """
        <div style="font-family: Arial, sans-serif; background-color: #4ca0e0; padding: 30px;">
          <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
            <h2 style="text-align: center; color: #2c3e50;">Bienvenue sur Voyager</h2>
            <p>Bonjour <strong>%s %s</strong>,</p>
            <p>Votre compte company a été créé avec succès.</p>

            <p><strong>Vos informations :</strong></p>
            <ul>
              <li><strong>ID company :</strong> %s</li>
              <li><strong>Identifiant (email) :</strong> %s</li>
              <li><strong>Mot de passe (temporaire) :</strong> %s</li>
            </ul>

            <p style="color: red;">⚠️ Veuillez modifier ce mot de passe lors de votre première connexion.</p>

            <p style="color: #888; font-size: 12px; text-align: center;">
              Si vous n'êtes pas à l'origine de cette création, ignorez ce message.
            </p>
            <p style="text-align: center; color: #aaa; margin-top: 20px;">— Voyager</p>
          </div>
        </div>
        """.formatted(
                    company.getName(),
                    company.getUsername(),
                    company.getId(),
                    company.getEmail(),
                    rawPassword
            );

            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }


    public void envoyerIdentifiantsManager(Manager manager, String rawPassword) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@solutech-one.com");
            helper.setTo(manager.getEmail());
            helper.setSubject("Vos identifiants Voyager");

            String html = """
        <div style="font-family: Arial, sans-serif; background-color: #4ca0e0; padding: 30px;">
          <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
            <h2 style="text-align: center; color: #2c3e50;">Bienvenue sur Voyager</h2>
            <p>Bonjour <strong>%s %s</strong>,</p>
            <p>Votre compte manager a été créé avec succès.</p>

            <p><strong>Vos informations :</strong></p>
            <ul>
              <li><strong>ID Manager :</strong> %s</li>
              <li><strong>Identifiant (email) :</strong> %s</li>
              <li><strong>Mot de passe (temporaire) :</strong> %s</li>
            </ul>

            <p style="color: red;">⚠️ Veuillez modifier ce mot de passe lors de votre première connexion.</p>

            <p style="color: #888; font-size: 12px; text-align: center;">
              Si vous n'êtes pas à l'origine de cette création, ignorez ce message.
            </p>
            <p style="text-align: center; color: #aaa; margin-top: 20px;">— SMS-GATEWAY</p>
          </div>
        </div>
        """.formatted(
                    manager.getNomManager(),
                    manager.getPrenomManager(),
                    manager.getIdManager(),
                    manager.getEmail(),
                    rawPassword
            );

            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            e.printStackTrace();
        }
    }

    @Value("${app.mail.from:noreply@solutech-one.com}")
    private String from; // centralise l'expéditeur
    /** 🔔 Mail : suspension d’un Manager */
    public void envoyerSuspensionManager(Manager manager) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(manager.getEmail());
            helper.setSubject("⚠️ Compte Manager suspendu — Voyager");

            String html = """
            <div style="font-family: Arial, sans-serif; background-color:#ffecec; padding:30px">
              <div style="max-width:600px;margin:auto;background:#fff;padding:25px;border-radius:8px;border:1px solid #e74c3c">
                <h2 style="color:#e74c3c; text-align:center;">Compte Manager suspendu</h2>
                <p>Bonjour <strong>%s %s</strong>,</p>
                <p>Votre compte <strong>ID : %s</strong> a été <span style="color:#e74c3c;">suspendu</span>.</p>
                <p>Vous n’avez plus accès à la plateforme jusqu’à résolution du problème.</p>
                <p>👉 Veuillez contacter l’administrateur ou le support pour plus d’informations.</p>
                <p style="text-align:center;color:#999;font-size:12px;margin-top:20px">— Voyager</p>
              </div>
            </div>
            """.formatted(
                    manager.getNomManager(),
                    manager.getPrenomManager(),
                    manager.getIdManager()
            );

            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    /** 🔔 Mail : réactivation d’un Manager */
    public void envoyerReactivationManager(Manager manager) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(manager.getEmail());
            helper.setSubject("✅ Compte Manager réactivé — Voyager");

            String html = """
            <div style="font-family: Arial, sans-serif; background-color:#e6f9ec; padding:30px">
              <div style="max-width:600px;margin:auto;background:#fff;padding:25px;border-radius:8px;border:1px solid #27ae60">
                <h2 style="color:#27ae60; text-align:center;">Compte Manager réactivé</h2>
                <p>Bonjour <strong>%s %s</strong>,</p>
                <p>Bonne nouvelle 🎉 ! Votre compte <strong>ID : %s</strong> a été <span style="color:#27ae60;">réactivé</span>.</p>
                <p>Vous pouvez à nouveau accéder à la plateforme et gérer vos opérations.</p>
                <p>Merci de votre collaboration.</p>
                <p style="text-align:center;color:#999;font-size:12px;margin-top:20px">— Voyager</p>
              </div>
            </div>
            """.formatted(
                    manager.getNomManager(),
                    manager.getPrenomManager(),
                    manager.getIdManager()
            );

            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}