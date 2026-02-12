package com.example.pasa_la_pagina.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SenGridService {

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${sendgrid.verify-email-template-id}")
    private String verifyEmailTemplateId;

    @Value("${sendgrid.reset-password-template-id}")
    private String resetPasswordTemplateId;

    public void sendVerificationEmail(String to, Map<String, Object> dynamicData) {
        sendEmailWithTemplate(to, verifyEmailTemplateId, dynamicData);
    }

    public void sendResetPasswordEmail(String to, Map<String, Object> dynamicData) {
        sendEmailWithTemplate(to, resetPasswordTemplateId, dynamicData);
    }

    private void sendEmailWithTemplate(String to, String templateId, Map<String, Object> dynamicData) {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        dynamicData.forEach((key, value) -> personalization.addDynamicTemplateData(key, value));
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("SendGrid status: " + response.getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
